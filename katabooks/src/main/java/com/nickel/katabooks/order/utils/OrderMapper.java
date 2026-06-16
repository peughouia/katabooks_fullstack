package com.nickel.katabooks.order.utils;

import com.nickel.katabooks.order.dto.OrderItemResponseDto;
import com.nickel.katabooks.order.dto.OrderResponseDto;
import com.nickel.katabooks.order.entity.Order;
import com.nickel.katabooks.order.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderResponseDto toOrderResponse(Order order) {
        List<OrderItemResponseDto> itemResponses = order.getItems().stream()
                .map(this::toOrderItemResponse)
                .toList();

        return new OrderResponseDto(
                order.getId(),
                order.getStatus(),
                order.calculateTotal(),
                order.getCreatedAt(),
                order.getConfirmedAt(),
                itemResponses
        );
    }

    private OrderItemResponseDto toOrderItemResponse(OrderItem item) {
        return new OrderItemResponseDto(
                item.getId(),
                item.getBook().getId(),
                item.getBook().getTitle(),
                item.getBook().getImageUrl(),
                item.getPriceAtPurchase(),
                item.getQuantity(),
                item.getPriceAtPurchase() * item.getQuantity()
        );
    }
}
