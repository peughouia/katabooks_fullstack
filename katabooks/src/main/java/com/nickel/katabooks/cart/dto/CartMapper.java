package com.nickel.katabooks.cart.dto;

import com.nickel.katabooks.cart.Cart;
import com.nickel.katabooks.cart.CartItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartMapper {

    public CartResponseDto toCartResponse(Cart cart){
        List<CartItemResponseDto> itemResponse = cart.getItems().stream()
                .map(this::toCartItemResponse)
                .toList();

        Double totalPrice = calculateTotalPrice(itemResponse);

        return new CartResponseDto(cart.getId(), itemResponse, totalPrice);
    }

    private CartItemResponseDto toCartItemResponse(CartItem item) {
        Double totalLinePrice = item.getBook().getPrice() * item.getQuantity();

        return new CartItemResponseDto(
                item.getId(),
                item.getBook().getId(),
                item.getBook().getTitle(),
                item.getBook().getImageUrl(),
                item.getBook().getPrice(),
                item.getQuantity(),
                totalLinePrice
        );
    }

    private Double calculateTotalPrice(List<CartItemResponseDto> items){
        return items.stream()
                .mapToDouble(CartItemResponseDto::getTotalLinePrice)
                .sum();
    }
}
