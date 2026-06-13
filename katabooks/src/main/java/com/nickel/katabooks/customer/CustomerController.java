package com.nickel.katabooks.customer;

import com.nickel.katabooks.customer.dto.CustomerLoginRequestDto;
import com.nickel.katabooks.customer.dto.CustomerLoginResponseDto;
import com.nickel.katabooks.customer.dto.CustomerRegisterRequestDto;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody CustomerRegisterRequestDto request){
        customerService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<CustomerLoginResponseDto> login(@Valid @RequestBody CustomerLoginRequestDto request) {
        CustomerLoginResponseDto response = customerService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bonjour")
    public String direBonjour() {
        return "Bonjour, l'application Spring Boot fonctionne parfaitement !";
    }

}
