package com.nickel.katabooks.order;


import com.nickel.katabooks.customer.Customer;
import com.nickel.katabooks.order.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Consulter son panier
    @GetMapping("/cart")
    public ResponseEntity<OrderResponseDto> getPanier(
            @RequestAttribute("authenticatedCustomer") Customer customer) {
        return ResponseEntity.ok(orderService.getPanier(customer));
    }

    // Ajouter un livre au panier
    @PostMapping("/cart/items")
    public ResponseEntity<OrderResponseDto> addToOrder(
            @RequestAttribute("authenticatedCustomer") Customer customer,
            @Valid @RequestBody AddToOrderRequestDto request) {
        return ResponseEntity.ok(orderService.addToOrder(customer, request));
    }

    // Modifier la quantité d'une ligne
    @PutMapping("/cart/items/{orderItemId}")
    public ResponseEntity<OrderResponseDto> updateOrderItem(
            @RequestAttribute("authenticatedCustomer") Customer customer,
            @PathVariable Long orderItemId,
            @Valid @RequestBody UpdateOrderItemRequestDto request) {
        return ResponseEntity.ok(orderService.updateOrderItem(customer, orderItemId, request));
    }

    // Supprimer une ligne
    @DeleteMapping("/cart/items/{orderItemId}")
    public ResponseEntity<OrderResponseDto> removeFromOrder(
            @RequestAttribute("authenticatedCustomer") Customer customer,
            @PathVariable Long orderItemId) {
        return ResponseEntity.ok(orderService.removeFromOrder(customer, orderItemId));
    }

    // Payer
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDto> checkout(
            @RequestAttribute("authenticatedCustomer") Customer customer,
            @Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.checkout(customer, request));
    }
}
