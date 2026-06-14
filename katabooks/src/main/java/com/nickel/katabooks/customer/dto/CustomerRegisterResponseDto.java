package com.nickel.katabooks.customer.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomerRegisterResponseDto {
    private String message;
    private String email;
    private String name;
}
