package com.nickel.katabooks.order.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddToOrderRequestDto {

    // l'utilisateur demande à ajouter un livre et sa quantité dans le panier/commande

    @NotNull(message = "L'identifiant du livre est obligatoire")
    private Long bookId;

    @NotNull
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantity;
}
