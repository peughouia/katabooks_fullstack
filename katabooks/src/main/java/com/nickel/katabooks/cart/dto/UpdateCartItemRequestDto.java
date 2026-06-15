package com.nickel.katabooks.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateCartItemRequestDto {
    @NotNull
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantity;
}
