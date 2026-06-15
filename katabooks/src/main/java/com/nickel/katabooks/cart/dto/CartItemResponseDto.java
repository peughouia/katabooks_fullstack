package com.nickel.katabooks.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartItemResponseDto {
    private Long cartItemId;
    private Long bookId;
    private String bookTitle;
    private String bookImageUrl;
    private Double bookPrice;
    private Integer quantity;
    private Double totalLinePrice;
}
