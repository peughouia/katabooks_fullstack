package com.nickel.katabooks.order.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateOrderItemRequestDto {

    // ici l'utilisateur demande à modifier la quantité d'un livre de son pagné
    @NotNull
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantity;
}
