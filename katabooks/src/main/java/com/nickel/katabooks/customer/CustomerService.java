package com.nickel.katabooks.customer;

import com.nickel.katabooks.customer.dto.CustomerLoginRequestDto;
import com.nickel.katabooks.customer.dto.CustomerLoginResponseDto;
import com.nickel.katabooks.customer.dto.CustomerRegisterRequestDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer register(CustomerRegisterRequestDto request){

        AvailableEmailVerify(request.getEmail());
        Customer customer = createCustomer(request);
        return customerRepository.save(customer);

    }

    public Customer registerGestionnaire(CustomerRegisterRequestDto request) {
        AvailableEmailVerify(request.getEmail());

        Customer customer = createCustomer(request);
        customer.setRole(Customer.Role.GESTIONNAIRE);

        return customerRepository.save(customer);
    }

    public CustomerLoginResponseDto login(CustomerLoginRequestDto request){
        Customer customer = foundCustomerViaEmail(request.getEmail());

        verifyPassword(request.getPassword(), customer.getPassword());

        String token = generateTokenForUser(customer);

        return new CustomerLoginResponseDto(token, customer.getRole(), customer.getEmail());
    }


    private void AvailableEmailVerify(String email) {
        if (customerRepository.existsByEmail(email)){
           throw new IllegalArgumentException("Un compte avec cet email existe déjà");
        }
    }

    private Customer createCustomer(CustomerRegisterRequestDto request){
        Customer customer = new Customer();
        customer.setEmail(request.getEmail());
        customer.setName(request.getName());
        String hashPassword = passwordEncoder.encode(request.getPassword());
        customer.setPassword(hashPassword);
        customer.setRole(Customer.Role.CLIENT);
        return customer;
    }

    private Customer foundCustomerViaEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe incorrect"));
    }

    private void verifyPassword(String inputPassword, String basePassword) {
        if (!passwordEncoder.matches(inputPassword,basePassword)) {
            throw new IllegalArgumentException("Email ou mot de passe incorrect");
        }
    }

    private String generateTokenForUser(Customer customer) {
        String token = UUID.randomUUID().toString();
        customer.setToken(token);
        customerRepository.save(customer);
        return token;
    }


}
