package com.nickel.katabooks.order.dto;

import com.nickel.katabooks.order.utils.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private OrderStatus status;
    private Double totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private List<OrderItemResponseDto> items;
}
