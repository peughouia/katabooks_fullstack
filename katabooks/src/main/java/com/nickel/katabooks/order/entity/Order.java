package com.nickel.katabooks.order.entity;

import com.nickel.katabooks.customer.Customer;
import com.nickel.katabooks.order.utils.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.CART;

    @Column
    private Double totalPrice;

    @Column
    private LocalDateTime confirmedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    //  Méthodes métier sur l'entité
    public Double calculateTotal() {
        return items.stream()
                .mapToDouble(item -> item.getPriceAtPurchase() * item.getQuantity())
                .sum();
    }

    public boolean estModifiable() {
        return this.status == OrderStatus.CART;
    }
}
