package com.nickel.katabooks.customer;

import com.nickel.katabooks.customer.dto.CustomerLoginRequestDto;
import com.nickel.katabooks.customer.dto.CustomerLoginResponseDto;
import com.nickel.katabooks.customer.dto.CustomerRegisterRequestDto;
import com.nickel.katabooks.customer.dto.CustomerRegisterResponseDto;
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
    public ResponseEntity<CustomerRegisterResponseDto> register(@Valid @RequestBody CustomerRegisterRequestDto request){
        Customer newcustomer = customerService.register(request);

        CustomerRegisterResponseDto reponse = new CustomerRegisterResponseDto(
                "Utilisateur créé avec succès",
                newcustomer.getEmail(),
                newcustomer.getName()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }

    @PostMapping("/register/gestionnaire")
    public ResponseEntity<Void> registerGestionnaire(
            @Valid @RequestBody CustomerRegisterRequestDto request) {
        customerService.registerGestionnaire(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<CustomerLoginResponseDto> login(@Valid @RequestBody CustomerLoginRequestDto request) {
        CustomerLoginResponseDto response = customerService.login(request);
        return ResponseEntity.ok(response);
    }

}
