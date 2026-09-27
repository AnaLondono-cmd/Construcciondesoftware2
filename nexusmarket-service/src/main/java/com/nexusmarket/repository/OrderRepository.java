package com.nexusmarket.repository;

import com.nexusmarket.domain.Order;
import com.nexusmarket.domain.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyer_Id(Long buyerId);
    List<Order> findByStatus(OrderStatus status);
}
