package com.nickel.katabooks;


import com.nickel.katabooks.book.BookRepository;
import com.nickel.katabooks.customer.Customer;

import com.nickel.katabooks.order.OrderService;
import com.nickel.katabooks.order.dto.OrderResponseDto;
import com.nickel.katabooks.order.entity.Order;
import com.nickel.katabooks.order.repository.OrderItemRepository;
import com.nickel.katabooks.order.repository.OrderRepository;
import com.nickel.katabooks.order.utils.CardValidator;
import com.nickel.katabooks.order.utils.OrderMapper;
import com.nickel.katabooks.order.utils.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceHistoryTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CardValidator cardValidator;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setEmail("test@nickel.eu");
        customer.setRole(Customer.Role.CLIENT);
    }

    @Test
    void shouldReturnPurchaseHistoryOrderedByDate() {
        // GIVEN
        Order order1 = new Order();
        order1.setId(1L);
        order1.setStatus(OrderStatus.CONFIRMED);
        order1.setItems(new ArrayList<>());

        Order order2 = new Order();
        order2.setId(2L);
        order2.setStatus(OrderStatus.CONFIRMED);
        order2.setItems(new ArrayList<>());

        when(orderRepository.findByCustomerAndStatusOrderByCreatedAtDesc(
                customer, OrderStatus.CONFIRMED))
                .thenReturn(List.of(order1, order2));

        when(orderMapper.toOrderResponse(order1))
                .thenReturn(new OrderResponseDto(1L, OrderStatus.CONFIRMED,
                        0.0, null, null, List.of()));
        when(orderMapper.toOrderResponse(order2))
                .thenReturn(new OrderResponseDto(2L, OrderStatus.CONFIRMED,
                        0.0, null, null, List.of()));

        // WHEN
        List<OrderResponseDto> result = orderService.getPurchaseHistory(customer);

        // THEN
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getOrderId());
        verify(orderRepository, times(1))
                .findByCustomerAndStatusOrderByCreatedAtDesc(customer, OrderStatus.CONFIRMED);
    }

    @Test
    void shouldReturnEmptyListWhenNoOrders() {
        // GIVEN
        when(orderRepository.findByCustomerAndStatusOrderByCreatedAtDesc(
                customer, OrderStatus.CONFIRMED))
                .thenReturn(List.of());

        // WHEN
        List<OrderResponseDto> result = orderService.getPurchaseHistory(customer);

        // THEN
        assertTrue(result.isEmpty());
    }
}
