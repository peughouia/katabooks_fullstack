package com.nickel.katabooks.order.dto;



import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemResponseDto {
    private Long orderItemId;
    private Long bookId;
    private String bookTitle;
    private String bookImageUrl;
    private Double priceAtPurchase;
    private Integer quantity;
    private Double totalLinePrice;
}
