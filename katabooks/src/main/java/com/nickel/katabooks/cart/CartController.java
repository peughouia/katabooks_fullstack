package com.nickel.katabooks.cart;

import com.nickel.katabooks.cart.dto.AddToCardRequestDto;
import com.nickel.katabooks.cart.dto.CartResponseDto;
import com.nickel.katabooks.cart.dto.UpdateCartItemRequestDto;
import com.nickel.katabooks.customer.Customer;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // GET /cart
    @GetMapping
    public ResponseEntity<CartResponseDto> getCart(
            @RequestAttribute("authenticatedCustomer") Customer customer) {
        return ResponseEntity.ok(cartService.getCart(customer));
    }

    // POST /cart/items
    @PostMapping("/items")
    public ResponseEntity<CartResponseDto> addToCart(
            @RequestAttribute("authenticatedCustomer") Customer customer,
            @Valid @RequestBody AddToCardRequestDto request) {
        return ResponseEntity.ok(cartService.addToCart(customer, request));
    }

    // PUT /cart/items/{cartItemId}
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponseDto> updateCartItem(
            @RequestAttribute("authenticatedCustomer") Customer customer,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequestDto request) {
        return ResponseEntity.ok(cartService.updateCartItem(customer, cartItemId, request));
    }

    // DELETE /cart/items/{cartItemId}
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponseDto> removeFromCart(
            @RequestAttribute("authenticatedCustomer") Customer customer,
            @PathVariable Long cartItemId) {
        return ResponseEntity.ok(cartService.removeFromCart(customer, cartItemId));
    }
}
