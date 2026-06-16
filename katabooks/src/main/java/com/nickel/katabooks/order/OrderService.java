package com.nickel.katabooks.order;


import com.nickel.katabooks.Exception.BookNotFoundException;
import com.nickel.katabooks.book.Book;
import com.nickel.katabooks.book.BookRepository;
import com.nickel.katabooks.customer.Customer;
import com.nickel.katabooks.order.dto.AddToOrderRequestDto;
import com.nickel.katabooks.order.dto.CheckoutRequest;
import com.nickel.katabooks.order.dto.OrderResponseDto;
import com.nickel.katabooks.order.dto.UpdateOrderItemRequestDto;
import com.nickel.katabooks.order.entity.Order;
import com.nickel.katabooks.order.entity.OrderItem;
import com.nickel.katabooks.order.repository.OrderItemRepository;
import com.nickel.katabooks.order.repository.OrderRepository;
import com.nickel.katabooks.order.utils.CardValidator;
import com.nickel.katabooks.order.utils.OrderMapper;
import com.nickel.katabooks.order.utils.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BookRepository bookRepository;
    private final CardValidator cardValidator;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        BookRepository bookRepository,
                        CardValidator cardValidator,
                        OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.bookRepository = bookRepository;
        this.cardValidator = cardValidator;
        this.orderMapper = orderMapper;
    }

    public OrderResponseDto getPanier(Customer customer) {
        Order order = findOrCreateCart(customer);
        return orderMapper.toOrderResponse(order);
    }

    @Transactional
    public OrderResponseDto addToOrder(Customer customer, AddToOrderRequestDto request) {
        // on vérifie que le livre existe et que le panier lié à l'user existe sinon on la crèe
        Book book = findBook(request.getBookId());
        Order order = findOrCreateCart(customer);
        checkcartEditable(order);

        // on vérifie si le livre existe deja dans la commande
        // si oui, on ajoute la quantité
        // si non, on ajoute cette ligne de commande avec le livre en question
        Optional<OrderItem> existingOrderItem =
                orderItemRepository.findByOrderAndBook(order, book);

        if (existingOrderItem.isPresent()) {
            incrementQuantity(existingOrderItem.get(), request.getQuantity(), book);
        } else {
            addNewOrderItem(order, book, request.getQuantity());
        }

        return orderMapper.toOrderResponse(order);
    }

    @Transactional
    public OrderResponseDto updateOrderItem(Customer customer,
                                         Long orderItemId,
                                         UpdateOrderItemRequestDto request) {
        OrderItem orderItem = findOrderItem(orderItemId);
        checkAppartenance(orderItem, customer);
        checkcartEditable(orderItem.getOrder());
        checkStock(orderItem.getBook(), request.getQuantity());

        orderItem.setQuantity(request.getQuantity());
        orderItemRepository.save(orderItem);

        return orderMapper.toOrderResponse(orderItem.getOrder());
    }

    @Transactional
    public OrderResponseDto removeFromOrder(Customer customer, Long orderItemId) {
        OrderItem orderItem = findOrderItem(orderItemId);
        checkAppartenance(orderItem, customer);
        checkcartEditable(orderItem.getOrder());

        Order order = orderItem.getOrder();
        order.getItems().remove(orderItem);
        orderRepository.save(order);

        return orderMapper.toOrderResponse(order);
    }


    @Transactional
    public OrderResponseDto checkout(Customer customer, CheckoutRequest request) {
        // on vérifie que la carte est valide,
        // on recherche le panier de l'utilisateur et qu'il n'est pas vide,
        // on modifie le stock du livre
        cardValidator.validate(request);
        Order order = findActiveCart(customer);
        checkCartNotEmpty(order);
        decrementerStocks(order);
        confirmCommande(order);

        return orderMapper.toOrderResponse(order);

    }

    public List<OrderResponseDto> getPurchaseHistory(Customer customer) {
        // 1. On récupère toutes les commandes payées du client
        List<Order> paidOrders = orderRepository
                .findByCustomerAndStatusOrderByCreatedAtDesc(customer, OrderStatus.CONFIRMED);

        // 2. On transforme chaque commande en DTO grâce au Mapper
        return paidOrders.stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    //   Méthodes privées

    private Order findOrCreateCart(Customer customer) {
        return orderRepository
                .findByCustomerAndStatus(customer, OrderStatus.CART)
                .orElseGet(() -> createCart(customer));
    }

    private Order createCart(Customer customer) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.CART);
        return orderRepository.save(order);
    }

    private Order findActiveCart(Customer customer) {
        return orderRepository
                .findByCustomerAndStatus(customer, OrderStatus.CART)
                .orElseThrow(() -> new IllegalArgumentException("Aucun panier actif trouvé"));
    }

    private Book findBook(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
    }

    private OrderItem findOrderItem(Long orderItemId) {
        return orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ligne introuvable avec l'id : " + orderItemId));
    }

    private void checkAppartenance(OrderItem ligne, Customer customer) {
        if (!ligne.getOrder().getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Cette ligne ne vous appartient pas");
        }
    }

    private void checkcartEditable(Order order) {
        if (!order.estModifiable()) {
            throw new IllegalArgumentException(
                    "Cette commande ne peut plus être modifiée");
        }
    }

    private void checkCartNotEmpty(Order order) {
        if (order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Votre panier est vide");
        }
    }

    private void checkStock(Book book, Integer quantiteDemandee) {
        if (book.getStock() < quantiteDemandee) {
            throw new IllegalArgumentException(
                    "Stock insuffisant. Disponible : " + book.getStock()
                            + ", demandé : " + quantiteDemandee);
        }
    }

    private void incrementQuantity(OrderItem orderItem,
                                     Integer quantityAdd,
                                     Book book) {
        int newQuantity = orderItem.getQuantity() + quantityAdd;
        checkStock(book, newQuantity);
        orderItem.setQuantity(newQuantity);
        orderItemRepository.save(orderItem);
    }

    private void addNewOrderItem(Order order, Book book, Integer quantity) {
        checkStock(book, quantity);

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setBook(book);
        item.setQuantity(quantity);
        item.setPriceAtPurchase(book.getPrice());

        order.getItems().add(item);
        orderItemRepository.save(item);
        orderRepository.save(order);
    }

    private void decrementerStocks(Order order) {
        for (OrderItem item : order.getItems()) {
            int newStock = item.getBook().getStock() - item.getQuantity();
            item.getBook().setStock(newStock);
        }
    }

    private void confirmCommande(Order order) {
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalPrice(order.calculateTotal());
        order.setConfirmedAt(LocalDateTime.now());
        orderRepository.save(order);
    }
}
