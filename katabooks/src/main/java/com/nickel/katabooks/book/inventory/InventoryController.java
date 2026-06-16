package com.nickel.katabooks.book.inventory;


import com.nickel.katabooks.customer.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<BookInventoryResponseDto>> getInventory(
            @RequestAttribute("authenticatedCustomer") Customer customer) {
        return ResponseEntity.ok(inventoryService.getInventory(customer));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookInventoryResponseDto> getBookInventory(
            @RequestAttribute("authenticatedCustomer") Customer customer,
            @PathVariable Long bookId) {
        return ResponseEntity.ok(inventoryService.getBookInventory(customer, bookId));
    }
}
