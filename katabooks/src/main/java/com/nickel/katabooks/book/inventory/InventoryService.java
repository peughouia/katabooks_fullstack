package com.nickel.katabooks.book.inventory;



import com.nickel.katabooks.Exception.BookNotFoundException;
import com.nickel.katabooks.book.Book;
import com.nickel.katabooks.book.BookRepository;
import com.nickel.katabooks.customer.Customer;
import com.nickel.katabooks.order.entity.OrderItem;
import com.nickel.katabooks.order.repository.OrderItemRepository;
import com.nickel.katabooks.order.utils.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final BookRepository bookRepository;
    private final OrderItemRepository orderItemRepository;

    public InventoryService(BookRepository bookRepository,
                            OrderItemRepository orderItemRepository) {
        this.bookRepository = bookRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public List<BookInventoryResponseDto> getInventory(Customer customer) {
        verifyRoleGestionnaire(customer);
        return bookRepository.findAll().stream()
                .map(this::toInventoryResponse)
                .toList();
    }

    public BookInventoryResponseDto getBookInventory(Customer customer, Long bookId) {
        verifyRoleGestionnaire(customer);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        return toInventoryResponse(book);
    }


    private void verifyRoleGestionnaire(Customer customer) {
        if (customer.getRole() != Customer.Role.GESTIONNAIRE) {
            throw new IllegalArgumentException("Accès refusé : rôle GESTIONNAIRE requis");
        }
    }
    private BookInventoryResponseDto toInventoryResponse(Book book) {
        List<OrderItem> lignesVendues = orderItemRepository
                .findByBookAndOrderStatus(book, OrderStatus.CONFIRMED);

        int nombreVendus = lignesVendues.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        return new BookInventoryResponseDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getStock(),
                nombreVendus
        );
    }
}
