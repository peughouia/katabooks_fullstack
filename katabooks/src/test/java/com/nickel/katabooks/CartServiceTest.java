package com.nickel.katabooks;

import com.nickel.katabooks.Exception.BookNotFoundException;
import com.nickel.katabooks.book.Book;
import com.nickel.katabooks.book.BookRepository;
import com.nickel.katabooks.cart.*;
import com.nickel.katabooks.cart.dto.AddToCardRequestDto;
import com.nickel.katabooks.cart.dto.CartMapper;
import com.nickel.katabooks.cart.dto.CartResponseDto;
import com.nickel.katabooks.cart.dto.UpdateCartItemRequestDto;
import com.nickel.katabooks.customer.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartService cartService;

    private Customer customer;
    private Book book;
    private Cart cart;

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

        cart = new Cart();
        cart.setId(1L);
        cart.setCustomer(customer);
        cart.setItems(new ArrayList<>());
    }

    @Test
    void shouldAddNewBookToCart(){
        AddToCardRequestDto request = new AddToCardRequestDto();
        request.setBookId(1L);
        request.setQuantity(2);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(cartRepository.findByCustomer(customer))
                .thenReturn(Optional.of(cart))
                .thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartAndBook(cart, book)).thenReturn(Optional.empty());
        when(cartMapper.toCartResponse(cart)).thenReturn(
                new CartResponseDto(1L, new ArrayList<>(), 0.0));

        CartResponseDto result = cartService.addToCart(customer, request);

        assertNotNull(result);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void shouldIncrementQuantityWhenBookAlreadyInCart() {
        CartItem existLine = new CartItem();
        existLine.setBook(book);
        existLine.setCart(cart);
        existLine.setQuantity(1);

        AddToCardRequestDto request = new AddToCardRequestDto();
        request.setBookId(1L);
        request.setQuantity(2);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(cartRepository.findByCustomer(customer))
                .thenReturn(Optional.of(cart))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCartAndBook(cart, book))
                .thenReturn(Optional.of(existLine));
        when(cartMapper.toCartResponse(cart)).thenReturn(
                new CartResponseDto(1L, new ArrayList<>(), 0.0));

        cartService.addToCart(customer, request);

        assertEquals(3, existLine.getQuantity());
        verify(cartItemRepository, times(1)).save(existLine);
    }

    @Test
    void shouldThrowWhenStockIsInsufficient() {

        book.setStock(1);

        AddToCardRequestDto request = new AddToCardRequestDto();
        request.setBookId(1L);
        request.setQuantity(5);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(cartRepository.findByCustomer(customer)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartAndBook(cart, book)).thenReturn(Optional.empty());


        assertThrows(IllegalArgumentException.class,
                () -> cartService.addToCart(customer, request));
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenBookNotFound() {
        // GIVEN
        AddToCardRequestDto request = new AddToCardRequestDto();
        request.setBookId(99L);
        request.setQuantity(1);

        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(BookNotFoundException.class,
                () -> cartService.addToCart(customer, request));
    }

    @Test
    void shouldUpdateCartItemQuantity() {
        CartItem line = new CartItem();
        line.setId(1L);
        line.setBook(book);
        line.setCart(cart);
        line.setQuantity(1);

        UpdateCartItemRequestDto request = new UpdateCartItemRequestDto();
        request.setQuantity(3);

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(line));
        when(cartRepository.findByCustomer(customer)).thenReturn(Optional.of(cart));
        when(cartMapper.toCartResponse(cart)).thenReturn(
                new CartResponseDto(1L, new ArrayList<>(), 0.0));

        cartService.updateCartItem(customer, 1L, request);

        // THEN
        assertEquals(3, line.getQuantity());
        verify(cartItemRepository, times(1)).save(line);
    }

    @Test
    void shouldThrowWhenUpdatingItemFromAnotherCustomer() {
        Customer autreClient = new Customer();
        autreClient.setId(99L);

        Cart autreCart = new Cart();
        autreCart.setCustomer(autreClient);

        CartItem line = new CartItem();
        line.setId(1L);
        line.setCart(autreCart);
        line.setBook(book);
        line.setQuantity(1);

        UpdateCartItemRequestDto request = new UpdateCartItemRequestDto();
        request.setQuantity(2);

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(line));

        // WHEN & THEN
        assertThrows(IllegalArgumentException.class,
                () -> cartService.updateCartItem(customer, 1L, request));
    }

}

