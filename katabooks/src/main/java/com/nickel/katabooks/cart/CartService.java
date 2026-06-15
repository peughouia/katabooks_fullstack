package com.nickel.katabooks.cart;

import com.nickel.katabooks.Exception.BookNotFoundException;
import com.nickel.katabooks.book.Book;
import com.nickel.katabooks.book.BookRepository;
import com.nickel.katabooks.cart.dto.AddToCardRequestDto;
import com.nickel.katabooks.cart.dto.CartMapper;
import com.nickel.katabooks.cart.dto.CartResponseDto;
import com.nickel.katabooks.cart.dto.UpdateCartItemRequestDto;
import com.nickel.katabooks.customer.Customer;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final CartMapper cartMapper;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       BookRepository bookRepository,
                       CartMapper cartMapper){
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.cartMapper = cartMapper;
    }

    public CartResponseDto getCart(Customer customer) {
        Cart cart = findOrCreateCart(customer);
        return cartMapper.toCartResponse(cart);
    }

    @Transactional
    public CartResponseDto addToCart(Customer customer, AddToCardRequestDto request){
        Book book = findBook(request.getBookId());
        Cart cart = findOrCreateCart(customer);

        Optional<CartItem> existLine = cartItemRepository.findByCartAndBook(cart, book);
        if (existLine.isPresent()){
            incrementQuantity(existLine.get(), request.getQuantity(), book);
        } else {
            addNewLine(cart, book, request.getQuantity());
        }

        Cart cartUpdate = cartRepository.findByCustomer(customer).orElseThrow();
        return cartMapper.toCartResponse(cartUpdate);
    }

    @Transactional
    public CartResponseDto updateCartItem(Customer customer,
                                       Long cartItemId,
                                       UpdateCartItemRequestDto request) {
        CartItem line = findLineCart(cartItemId);
        verifyAppartenance(line, customer);
        verifyStock(line.getBook(), request.getQuantity());

        line.setQuantity(request.getQuantity());
        cartItemRepository.save(line);

        Cart cart = findOrCreateCart(customer);
        return cartMapper.toCartResponse(cart);
    }

    @Transactional
    public CartResponseDto removeFromCart(Customer customer, Long cartItemId) {
        CartItem line = findLineCart(cartItemId);
        verifyAppartenance(line, customer);

        Cart cart = line.getCart();
        cart.getItems().remove(line);
        cartRepository.save(cart);

        return cartMapper.toCartResponse(cart);
    }


    private Cart findOrCreateCart(Customer customer) {
        return cartRepository.findByCustomer(customer)
                .orElseGet(() -> createCart(customer));
    }

    private Cart createCart(Customer customer) {
        Cart cart = new Cart();
        cart.setCustomer(customer);
        return cartRepository.save(cart);
    }

    private Book findBook(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
    }

    private CartItem findLineCart(Long cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ligne de panier introuvable avec l'id : " + cartItemId));
    }

    private void verifyAppartenance(CartItem line, Customer customer) {
        if (!line.getCart().getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Cette ligne ne vous appartient pas");
        }
    }

    private void verifyStock(Book book, Integer quantiteDemandee) {
        if (book.getStock() < quantiteDemandee) {
            throw new IllegalArgumentException(
                    "Stock insuffisant. Disponible : " + book.getStock()
                            + ", demandé : " + quantiteDemandee);
        }
    }

    private void incrementQuantity(CartItem line, Integer quantiteAjouter, Book book) {
        int nouvelleQuantite = line.getQuantity() + quantiteAjouter;
        verifyStock(book, nouvelleQuantite);
        line.setQuantity(nouvelleQuantite);
        cartItemRepository.save(line);
    }

    private void addNewLine(Cart cart, Book book, Integer quantity) {
        verifyStock(book, quantity);
        CartItem nouvelleLigne = new CartItem();
        nouvelleLigne.setCart(cart);
        nouvelleLigne.setBook(book);
        nouvelleLigne.setQuantity(quantity);
        cartItemRepository.save(nouvelleLigne);
    }
}
