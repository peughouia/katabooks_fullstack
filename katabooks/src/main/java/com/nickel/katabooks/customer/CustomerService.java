package com.nickel.katabooks.customer;

import com.nickel.katabooks.customer.dto.CustomerLoginRequestDto;
import com.nickel.katabooks.customer.dto.CustomerLoginResponseDto;
import com.nickel.katabooks.customer.dto.CustomerRegisterRequestDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer register(CustomerRegisterRequestDto request){

        AvailableEmailVerify(request.getEmail());
        Customer customer = createCustomer(request);
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
        customer.setPassword(request.getPassword());
        customer.setRole(Customer.Role.CLIENT);
        return customer;
    }

    private Customer foundCustomerViaEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe incorrect"));
    }

    private void verifyPassword(String inputPassword, String basePassword) {
        if (!inputPassword.equals(basePassword)) {
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
