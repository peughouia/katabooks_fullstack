package com.nickel.katabooks.order.repository;

import com.nickel.katabooks.customer.Customer;
import com.nickel.katabooks.order.entity.Order;
import com.nickel.katabooks.order.utils.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByCustomerAndStatus(Customer customer, OrderStatus status);
}
