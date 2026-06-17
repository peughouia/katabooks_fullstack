package com.nickel.katabooks.auth;

import com.nickel.katabooks.customer.Customer;
import com.nickel.katabooks.customer.CustomerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final CustomerRepository customerRepository;

    public AuthInterceptor(CustomerRepository customerRepository) {

        this.customerRepository = customerRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception{

        String token = extractToken(request);
        if (token == null){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token manquant");
            return false;
        }

        Optional<Customer> customer = customerRepository.findByToken(token);

        if (customer.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token invalide");
            return false;
        }

        request.setAttribute("authenticatedCustomer", customer.get());
        return true;
    }

    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isBlank()) {
            return null;
        }
        return token;
    }

}
