package com.nexusmarket.service;

import com.nexusmarket.domain.Inventory;
import com.nexusmarket.domain.Order;
import com.nexusmarket.domain.enums.OrderStatus;

import java.util.List;

public interface ReportService {

    List<Order> reportOrdersByStatus(OrderStatus status);

    List<Inventory> reportInventoryByWarehouse(Long warehouseId);

    long countActiveOrders();
}
