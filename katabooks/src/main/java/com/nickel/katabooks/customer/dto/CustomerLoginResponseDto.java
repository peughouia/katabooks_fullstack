package com.nickel.katabooks.customer.dto;


import com.nickel.katabooks.customer.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerLoginResponseDto {
    private String token;
    private Customer.Role role;
    private String email;
}
