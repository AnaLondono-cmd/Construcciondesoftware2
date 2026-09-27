package com.nexusmarket.service;

import com.nexusmarket.domain.Order;

import java.util.List;

/**
 * Core application service: Order Management domain.
 * Orchestrates Cart -> Order -> Inventory -> Invoice -> Shipment, following the order lifecycle
 * described in the functional specification ("Order Status Cycle").
 */
public interface OrderService {

    /** Use case: the buyer confirms the order (Business Flow step 5 -> 6). */
    Order confirmOrder(Long buyerId, Long dispatchWarehouseId);

    /** Use case: payment is validated (Business Flow step 6), issuing the invoice. */
    Order confirmPayment(Long orderId);

    /** Use case: packing and dispatch of the order (Business Flow step 7). */
    Order ship(Long orderId, Long dispatchWarehouseId);

    /** Use case: closing — the order is marked as completed after delivery is confirmed (Business Flow step 8). */
    Order complete(Long orderId);

    Order cancel(Long orderId, Long dispatchWarehouseId);

    List<Order> getPurchaseHistory(Long buyerId);

    Order getOrder(Long orderId);
}
