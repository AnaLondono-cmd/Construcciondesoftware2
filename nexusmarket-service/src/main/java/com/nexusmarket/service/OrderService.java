package com.nexusmarket.service;

import com.nexusmarket.domain.Order;

import java.util.List;


public interface OrderService {

    Order confirmOrder(Long buyerId, Long dispatchWarehouseId);

    Order confirmPayment(Long orderId);

    Order ship(Long orderId, Long dispatchWarehouseId);

    Order complete(Long orderId);

    Order cancel(Long orderId, Long dispatchWarehouseId);

    List<Order> getPurchaseHistory(Long buyerId);

    Order getOrder(Long orderId);
}
