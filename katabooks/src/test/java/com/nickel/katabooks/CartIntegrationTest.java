package com.nickel.katabooks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nickel.katabooks.book.Book;
import com.nickel.katabooks.book.BookRepository;
import com.nickel.katabooks.book.utils.IsbnGenerator;
import com.nickel.katabooks.cart.CartRepository;
import com.nickel.katabooks.cart.dto.AddToCardRequestDto;
import com.nickel.katabooks.cart.dto.UpdateCartItemRequestDto;
import com.nickel.katabooks.customer.Customer;
import com.nickel.katabooks.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CartIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CartRepository cartRepository;

    private String customerToken;
    private Long bookId;

    @BeforeEach
    void setUp(){
        cartRepository.deleteAll();
        customerRepository.deleteAll();
        bookRepository.deleteAll();

        // Créer le client avec un token
        Customer customer = new Customer();
        customer.setEmail("cart@nickel.eu");
        customer.setPassword("secret123");
        customer.setRole(Customer.Role.CLIENT);
        customer.setToken("token-test-cart");
        customerRepository.save(customer);
        customerToken = "token-test-cart";


        // Créer un livre avec stock
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
    void shouldReturn200WithEmptyCartOnFirstAccess() throws Exception {
        mockMvc.perform(get("/cart")
                        .header("Authorization", customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.totalPrice").value(0.0));
    }

    @Test
    void shouldAddBookToCart() throws Exception {
        AddToCardRequestDto request = new AddToCardRequestDto();
        request.setBookId(bookId);
        request.setQuantity(2);

        mockMvc.perform(post("/cart/items")
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.totalPrice").value(29.98));
    }

    @Test
    void shouldReturn400WhenStockIsInsufficient() throws Exception {
        AddToCardRequestDto request = new AddToCardRequestDto();
        request.setBookId(bookId);
        request.setQuantity(99);

        mockMvc.perform(post("/cart/items")
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreur").exists());
    }

    @Test
    void shouldReturn401WhenNoTokenProvided() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldUpdateCartItemQuantity() throws Exception {
        AddToCardRequestDto addRequest = new AddToCardRequestDto();
        addRequest.setBookId(bookId);
        addRequest.setQuantity(1);

        String addResponse = mockMvc.perform(post("/cart/items")
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(addResponse);
        JsonNode itemsArray = root.get("items");

        if (itemsArray == null || itemsArray.isEmpty()) {
            throw new IllegalStateException("CRASH DU TEST : L'article n'a pas été ajouté au panier ! " +
                    "Voici ce que le serveur a renvoyé : " + addResponse);
        }
        Long cartItemId = itemsArray.get(0).get("cartItemId").asLong();

        // Modifier la quantité
        UpdateCartItemRequestDto updateRequest = new UpdateCartItemRequestDto();
        updateRequest.setQuantity(3);

        mockMvc.perform(put("/cart/items/" + cartItemId)
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity").value(3));
    }

    @Test
    void shouldRemoveItemFromCart() throws Exception {
        AddToCardRequestDto addRequest = new AddToCardRequestDto();
        addRequest.setBookId(bookId);
        addRequest.setQuantity(1);

        String addResponse = mockMvc.perform(post("/cart/items")
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(addResponse);
        JsonNode itemsArray = root.get("items");

        if (itemsArray == null || itemsArray.isEmpty()) {
            throw new IllegalStateException("CRASH DU TEST : L'article n'a pas été ajouté au panier ! " +
                    "Voici ce que le serveur a renvoyé : " + addResponse);
        }
        Long cartItemId = itemsArray.get(0).get("cartItemId").asLong();

        mockMvc.perform(delete("/cart/items/" + cartItemId)
                        .header("Authorization", customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0));
    }

}
