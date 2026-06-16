package com.nickel.katabooks;

import com.nickel.katabooks.book.Book;
import com.nickel.katabooks.book.BookRepository;
import com.nickel.katabooks.customer.Customer;
import com.nickel.katabooks.order.*;
import com.nickel.katabooks.order.dto.*;
import com.nickel.katabooks.order.entity.Order;
import com.nickel.katabooks.order.entity.OrderItem;
import com.nickel.katabooks.order.repository.OrderItemRepository;
import com.nickel.katabooks.order.repository.OrderRepository;
import com.nickel.katabooks.order.utils.CardValidator;
import com.nickel.katabooks.order.utils.OrderMapper;
import com.nickel.katabooks.order.utils.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private BookRepository bookRepository;
    @Mock private CardValidator cardValidator;
    @Mock private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private Book book;
    private Order orderCart;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setEmail("test@nickel.eu");
        customer.setRole(Customer.Role.CLIENT);

        book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setPrice(14.99);
        book.setStock(5);

        orderCart = new Order();
        orderCart.setId(1L);
        orderCart.setCustomer(customer);
        orderCart.setStatus(OrderStatus.CART);
        orderCart.setItems(new ArrayList<>());
    }


    @Test
    void shouldCreateCartWhenNoneExists() {
        // GIVEN
        when(orderRepository.findByCustomerAndStatus(customer, OrderStatus.CART))
                .thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenReturn(orderCart);
        when(orderMapper.toOrderResponse(any())).thenReturn(
                new OrderResponseDto(1L, OrderStatus.CART, 0.0, null, null, List.of()));

        OrderResponseDto result = orderService.getPanier(customer);

        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void shouldAddBookToCart() {
        // GIVEN
        AddToOrderRequestDto request = new AddToOrderRequestDto();
        request.setBookId(1L);
        request.setQuantity(2);
        OrderResponseDto expectedResponse = new OrderResponseDto(1L, OrderStatus.CART, 29.98, null, null, List.of());

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(orderRepository.findByCustomerAndStatus(customer, OrderStatus.CART))
                .thenReturn(Optional.of(orderCart));
        when(orderItemRepository.findByOrderAndBook(orderCart, book))
                .thenReturn(Optional.empty());
        when(orderMapper.toOrderResponse(orderCart))
                .thenReturn(expectedResponse);
        // WHEN
        OrderResponseDto result = orderService.addToOrder(customer, request);

        // THEN
        assertNotNull(result);
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    void shouldThrowWhenStockInsufficient() {
        // GIVEN
        book.setStock(1);

        AddToOrderRequestDto request = new AddToOrderRequestDto();
        request.setBookId(1L);
        request.setQuantity(5);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(orderRepository.findByCustomerAndStatus(customer, OrderStatus.CART))
                .thenReturn(Optional.of(orderCart));
        when(orderItemRepository.findByOrderAndBook(orderCart, book))
                .thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(IllegalArgumentException.class,
                () -> orderService.addToOrder(customer, request));
        verify(orderItemRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenOrderIsNotModifiable() {
        // GIVEN — la ligne appartient à une commande déjà CONFIRMED
        orderCart.setStatus(OrderStatus.CONFIRMED);

        OrderItem ligne = new OrderItem();
        ligne.setId(1L);
        ligne.setOrder(orderCart);
        ligne.setBook(book);
        ligne.setQuantity(1);

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(ligne));

        UpdateOrderItemRequestDto updateRequest = new UpdateOrderItemRequestDto();
        updateRequest.setQuantity(2);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.updateOrderItem(customer, 1L, updateRequest));

        // Vérifie qu'on n'a jamais tenté de sauvegarder
        verify(orderItemRepository, never()).save(any());
    }


    @Test
    void shouldCheckoutSuccessfully() {
        // GIVEN
        OrderItem item = new OrderItem();
        item.setBook(book);
        item.setQuantity(2);
        item.setPriceAtPurchase(14.99);
        item.setOrder(orderCart);
        orderCart.getItems().add(item);

        CheckoutRequest request = new CheckoutRequest();
        request.setCardNumber("1234567890123456");
        request.setExpiryDate("12/99");
        request.setCvv("123");

        when(orderRepository.findByCustomerAndStatus(customer, OrderStatus.CART))
                .thenReturn(Optional.of(orderCart));
        when(orderRepository.save(any())).thenReturn(orderCart);
        when(orderMapper.toOrderResponse(any())).thenReturn(
                new OrderResponseDto(1L, OrderStatus.CONFIRMED, 29.98, null, null, List.of()));

        // WHEN
        OrderResponseDto result = orderService.checkout(customer, request);

        // THEN
        assertEquals(OrderStatus.CONFIRMED, orderCart.getStatus());
        assertEquals(3, book.getStock());
        verify(cardValidator, times(1)).validate(request);
        verify(orderRepository, times(1)).save(orderCart);
    }

    @Test
    void shouldThrowWhenCartIsEmpty() {
        // GIVEN — panier vide
        CheckoutRequest request = new CheckoutRequest();
        request.setCardNumber("1234567890123456");
        request.setExpiryDate("12/99");
        request.setCvv("123");

        when(orderRepository.findByCustomerAndStatus(customer, OrderStatus.CART))
                .thenReturn(Optional.of(orderCart));

        // WHEN & THEN
        assertThrows(IllegalArgumentException.class,
                () -> orderService.checkout(customer, request));
        verify(orderRepository, never()).save(any());
    }
}