package com.nickel.katabooks;


import com.nickel.katabooks.customer.Customer;
import com.nickel.katabooks.customer.CustomerRepository;
import com.nickel.katabooks.customer.dto.CustomerLoginRequestDto;
import com.nickel.katabooks.customer.dto.CustomerRegisterRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void cleanDatabase() {
        customerRepository.deleteAll();
    }

    @Test
    void shouldReturn201AndSaveToDatabaseWhenRegisteringNewCustomer() throws Exception {
        CustomerRegisterRequestDto request = new CustomerRegisterRequestDto();
        request.setEmail("new@nickel.eu");
        request.setName("nickel bank");
        request.setPassword("secret123");

        mockMvc.perform(post("/customers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        Optional<Customer> savedCustomer = customerRepository.findByEmail("new@nickel.eu");
        assertTrue(savedCustomer.isPresent(), "Le client doit être sauvegardé en base");

        assertNotEquals("secret123", savedCustomer.get().getPassword());
    }

    @Test
    void shouldReturn400WhenRegisteringWithExistingEmail() throws Exception {
        // GIVEN - On crée un compte initial
        CustomerRegisterRequestDto request = new CustomerRegisterRequestDto();
        request.setEmail("duplicate@nickel.eu");
        request.setName("nickel bank");
        request.setPassword("secret123");

        mockMvc.perform(post("/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // WHEN - On essaie de s'inscrire avec le même email
        // THEN - On attend une erreur 400 (Nécessite le @RestControllerAdvice dans ton code !)
        mockMvc.perform(post("/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenRegisteringWithInvalidData() throws Exception {
        // GIVEN - Un email au mauvais format et un mot de passe vide
        CustomerRegisterRequestDto request = new CustomerRegisterRequestDto();
        request.setEmail("ceci_nest_pas_un_email");
        request.setName("nickel bank");
        request.setPassword("");

        // WHEN & THEN - Spring Boot Validation (@Valid) doit bloquer la requête
        mockMvc.perform(post("/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200WithTokenWhenLoginSuccessful() throws Exception {
        // GIVEN - Un compte existant
        CustomerRegisterRequestDto registerRequest = new CustomerRegisterRequestDto();
        registerRequest.setEmail("login@nickel.eu");
        registerRequest.setName("nickel bank");
        registerRequest.setPassword("secret123");

        mockMvc.perform(post("/customers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        CustomerLoginRequestDto loginRequest = new CustomerLoginRequestDto();
        loginRequest.setEmail("login@nickel.eu");
        loginRequest.setPassword("secret123");

        // WHEN
        mockMvc.perform(post("/customers/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))

                // THEN - Vérification du statut et du contenu JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("CLIENT"))
                .andExpect(jsonPath("$.email").value("login@nickel.eu"));
    }

    @Test
    void shouldReturn400WhenLoginWithWrongPassword() throws Exception {
        // GIVEN - Un compte existant
        CustomerRegisterRequestDto registerRequest = new CustomerRegisterRequestDto();
        registerRequest.setEmail("login@nickel.eu");
        registerRequest.setName("nickel bank");
        registerRequest.setPassword("secret123");

        mockMvc.perform(post("/customers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // Le client se trompe de mot de passe
        CustomerLoginRequestDto loginRequest = new CustomerLoginRequestDto();
        loginRequest.setEmail("login@nickel.eu");
        loginRequest.setPassword("mauvais_mot_de_passe");

        // WHEN & THEN - La connexion doit être refusée
        mockMvc.perform(post("/customers/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest()); // ou isUnauthorized() selon ton implémentation
    }

    @Test
    void shouldReturn400WhenLoginWithUnknownEmail() throws Exception {
        // GIVEN - Un email qui n'existe pas en base
        CustomerLoginRequestDto loginRequest = new CustomerLoginRequestDto();
        loginRequest.setEmail("inconnu@nickel.eu");
        loginRequest.setPassword("secret123");

        // WHEN & THEN - La connexion doit échouer directement
        mockMvc.perform(post("/customers/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

}
