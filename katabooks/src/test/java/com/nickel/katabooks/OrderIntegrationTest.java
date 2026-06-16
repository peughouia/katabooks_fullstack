package com.nickel.katabooks;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nickel.katabooks.book.Book;
import com.nickel.katabooks.book.BookRepository;
import com.nickel.katabooks.book.utils.IsbnGenerator;
import com.nickel.katabooks.customer.Customer;
import com.nickel.katabooks.customer.CustomerRepository;
import com.nickel.katabooks.order.dto.AddToOrderRequestDto;
import com.nickel.katabooks.order.dto.CheckoutRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderIntegrationTest {

    @Autowired private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired private CustomerRepository customerRepository;
    @Autowired private BookRepository bookRepository;


    private String token;
    private Long bookId;

    @BeforeEach
    void setUp() {
        Customer customer = new Customer();
        customer.setEmail("order@nickel.eu");
        customer.setPassword("secret123");
        customer.setRole(Customer.Role.CLIENT);
        customer.setToken("token-order-it");
        customerRepository.save(customer);
        token = "token-order-it";

        Book book = new Book();
        book.setTitle("Clean Code");
        book.setIsbn(IsbnGenerator.generateFakeIsbn13());
        book.setAuthor("Robert Martin");
        book.setSummary("Résumé");
        book.setDescription("Description");
        book.setNumberOfPages(200);
        book.setStock(5);
        book.setPrice(14.99);
        bookId = bookRepository.save(book).getId();
    }

    @Test
    void shouldGetEmptyCartOnFirstAccess() throws Exception {
        mockMvc.perform(get("/orders/cart")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CART"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(0));
    }

    @Test
    void shouldAddBookToCart() throws Exception {
        AddToOrderRequestDto request = new AddToOrderRequestDto();
        request.setBookId(bookId);
        request.setQuantity(2);

        mockMvc.perform(post("/orders/cart/items")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CART"))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    void shouldReturn400WhenStockInsufficient() throws Exception {
        AddToOrderRequestDto request = new AddToOrderRequestDto();
        request.setBookId(bookId);
        request.setQuantity(999);

        mockMvc.perform(post("/orders/cart/items")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldCheckoutSuccessfully() throws Exception {
        // Ajouter un livre
        AddToOrderRequestDto addRequest = new AddToOrderRequestDto();
        addRequest.setBookId(bookId);
        addRequest.setQuantity(2);

        mockMvc.perform(post("/orders/cart/items")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        // Payer
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setCardNumber("1234567890123456");
        checkoutRequest.setExpiryDate("12/99");
        checkoutRequest.setCvv("123");

        mockMvc.perform(post("/orders/checkout")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkoutRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.totalPrice").value(29.98));

        // Vérifier que le stock a été décrémenté
        Book bookApresAchat = bookRepository.findById(bookId).orElseThrow();
        assertEquals(3, bookApresAchat.getStock());
    }

    @Test
    void shouldReturn400WhenCardRefused() throws Exception {
        // Ajouter un livre d'abord
        AddToOrderRequestDto addRequest = new AddToOrderRequestDto();
        addRequest.setBookId(bookId);
        addRequest.setQuantity(1);

        mockMvc.perform(post("/orders/cart/items")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        // Carte refusée
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setCardNumber("0000000000000000");
        checkoutRequest.setExpiryDate("12/99");
        checkoutRequest.setCvv("123");

        mockMvc.perform(post("/orders/checkout")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkoutRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Carte bancaire refusée"));
    }

    @Test
    void shouldReturn401WhenNoToken() throws Exception {
        mockMvc.perform(get("/orders/cart"))
                .andExpect(status().isUnauthorized());
    }
}