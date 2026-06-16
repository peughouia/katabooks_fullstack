package com.nickel.katabooks.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CheckoutRequest {

    // les données de la carte bancaire que l'utilisateur entre

    @NotBlank(message = "Le numéro de carte est obligatoire")
    @Pattern(regexp = "\\d{16}", message = "Le numéro de carte doit contenir 16 chiffres")
    private String cardNumber;

    @NotBlank(message = "La date d'expiration est obligatoire")
    @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "Format attendu : MM/YY")
    private String expiryDate;

    @NotBlank(message = "Le CVV est obligatoire")
    @Pattern(regexp = "\\d{3}", message = "Le CVV doit contenir 3 chiffres")
    private String cvv;
}
