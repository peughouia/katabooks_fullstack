package com.nickel.katabooks.order.repository;

import com.nickel.katabooks.book.Book;
import com.nickel.katabooks.order.entity.Order;
import com.nickel.katabooks.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    Optional<OrderItem> findByOrderAndBook(Order order, Book book);
}