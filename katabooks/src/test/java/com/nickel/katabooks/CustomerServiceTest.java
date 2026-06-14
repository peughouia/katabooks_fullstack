package com.nickel.katabooks;

import com.nickel.katabooks.customer.Customer;
import com.nickel.katabooks.customer.CustomerRepository;
import com.nickel.katabooks.customer.CustomerService;
import com.nickel.katabooks.customer.dto.CustomerLoginRequestDto;
import com.nickel.katabooks.customer.dto.CustomerLoginResponseDto;
import com.nickel.katabooks.customer.dto.CustomerRegisterRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void shouldRegisterCustomerSuccessfully(){
        // préparation donnée
        CustomerRegisterRequestDto request = new CustomerRegisterRequestDto();
        request.setEmail("test@nickel.com");
        request.setName("nickel bank");
        request.setPassword("nickelpassword");

        // service a testé
        when(customerRepository.existsByEmail("test@nickel.com")).thenReturn(false);
        when(passwordEncoder.encode("nickelpassword")).thenReturn("mot_de_passe_hache_123");
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));
        Customer result = customerService.register(request);

        //resultat qu'on recherche
        assertEquals("test@nickel.com", result.getEmail());
        assertEquals("mot_de_passe_hache_123", result.getPassword());
        assertEquals(Customer.Role.CLIENT, result.getRole());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        CustomerRegisterRequestDto request = new CustomerRegisterRequestDto();
        request.setEmail("existing@nickel.com");
        request.setPassword("nickelpassword");

        when(customerRepository.existsByEmail("existing@nickel.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> customerService.register(request));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldLoginSuccessfully(){
        Customer customer = new Customer();
        customer.setEmail("test@nickel.com");
        customer.setPassword("mot_de_passe_hache_123");
        customer.setRole(Customer.Role.CLIENT);

        // Input utilisateur
        CustomerLoginRequestDto request = new CustomerLoginRequestDto();
        request.setEmail("test@nickel.com");
        request.setPassword("nickelpassword");

        when(customerRepository.findByEmail("test@nickel.com")).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("nickelpassword", "mot_de_passe_hache_123")).thenReturn(true);
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));
        CustomerLoginResponseDto response = customerService.login(request);

        assertNotNull(response.getToken());
        assertEquals("test@nickel.com", response.getEmail());
        assertEquals(Customer.Role.CLIENT, response.getRole());
    }

    @Test
    void shouldThrowWhenPasswordIsWrong() {
        Customer customer = new Customer();
        customer.setEmail("test@nickel.com");
        customer.setPassword("mot_de_passe_hache_123");

        CustomerLoginRequestDto request = new CustomerLoginRequestDto();
        request.setEmail("test@nickel.com");
        request.setPassword("mauvaismdp");

        when(customerRepository.findByEmail("test@nickel.com")).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("mauvaismdp", "mot_de_passe_hache_123")).thenReturn(false);


        assertThrows(IllegalArgumentException.class, () -> customerService.login(request));
    }

    @Test
    void shouldThrowWhenEmailNotFound() {
        // GIVEN
        CustomerLoginRequestDto request = new CustomerLoginRequestDto();
        request.setEmail("inconnu@nickel.com");
        request.setPassword("secret123");

        when(customerRepository.findByEmail("inconnu@nickel.com")).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(IllegalArgumentException.class, () -> customerService.login(request));
    }
}
