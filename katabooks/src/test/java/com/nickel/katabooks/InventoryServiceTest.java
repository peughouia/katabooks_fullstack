package com.nickel.katabooks;

import com.nickel.katabooks.Exception.BookNotFoundException;
import com.nickel.katabooks.book.Book;
import com.nickel.katabooks.book.BookRepository;
import com.nickel.katabooks.book.inventory.BookInventoryResponseDto;
import com.nickel.katabooks.book.inventory.InventoryService;
import com.nickel.katabooks.customer.Customer;
import com.nickel.katabooks.order.entity.OrderItem;
import com.nickel.katabooks.order.repository.OrderItemRepository;
import com.nickel.katabooks.order.utils.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Customer gestionnaire;
    private Customer client;
    private Book book;

    @BeforeEach
    void setUp() {
        gestionnaire = new Customer();
        gestionnaire.setId(1L);
        gestionnaire.setEmail("gestionnaire@nickel.eu");
        gestionnaire.setRole(Customer.Role.GESTIONNAIRE);

        client = new Customer();
        client.setId(2L);
        client.setEmail("client@nickel.eu");
        client.setRole(Customer.Role.CLIENT);

        book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setAuthor("Robert Martin");
        book.setStock(3);
    }

    @Test
    void shouldReturnInventoryForGestionnaire() {
        // GIVEN
        OrderItem item = new OrderItem();
        item.setBook(book);
        item.setQuantity(2);

        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(orderItemRepository.findByBookAndOrderStatus(book, OrderStatus.CONFIRMED))
                .thenReturn(List.of(item));

        // WHEN
        List<BookInventoryResponseDto> result = inventoryService.getInventory(gestionnaire);

        // THEN
        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getStockDisponible());
        assertEquals(2, result.get(0).getNombreVendus());
        assertEquals("Clean Code", result.get(0).getTitle());
    }

    @Test
    void shouldThrowWhenClientTriesToAccessInventory() {
        // WHEN & THEN
        assertThrows(IllegalArgumentException.class,
                () -> inventoryService.getInventory(client));

        verify(bookRepository, never()).findAll();
    }

    @Test
    void shouldReturnZeroSoldWhenNoSales() {
        // GIVEN
        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(orderItemRepository.findByBookAndOrderStatus(book, OrderStatus.CONFIRMED))
                .thenReturn(List.of());

        // WHEN
        List<BookInventoryResponseDto> result = inventoryService.getInventory(gestionnaire);

        // THEN
        assertEquals(0, result.get(0).getNombreVendus());
    }

    @Test
    void shouldReturnBookInventoryForGestionnaire() {
        // GIVEN
        OrderItem item1 = new OrderItem();
        item1.setQuantity(3);

        OrderItem item2 = new OrderItem();
        item2.setQuantity(2);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(orderItemRepository.findByBookAndOrderStatus(book, OrderStatus.CONFIRMED))
                .thenReturn(List.of(item1, item2));

        // WHEN
        BookInventoryResponseDto result = inventoryService.getBookInventory(gestionnaire, 1L);

        // THEN
        assertEquals(3, result.getStockDisponible());
        assertEquals(5, result.getNombreVendus());
    }

    @Test
    void shouldThrowWhenBookNotFound() {
        // GIVEN
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(BookNotFoundException.class,
                () -> inventoryService.getBookInventory(gestionnaire, 99L));
    }

    @Test
    void shouldThrowWhenClientTriesToAccessBookInventory() {
        // WHEN & THEN
        assertThrows(IllegalArgumentException.class,
                () -> inventoryService.getBookInventory(client, 1L));

        verify(bookRepository, never()).findById(any());
    }
}
