package com.nexusmarket.service.impl;

import com.nexusmarket.domain.Inventory;
import com.nexusmarket.domain.Order;
import com.nexusmarket.domain.enums.OrderStatus;
import com.nexusmarket.repository.InventoryRepository;
import com.nexusmarket.repository.OrderRepository;
import com.nexusmarket.service.ReportService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;

    public ReportServiceImpl(OrderRepository orderRepository, InventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public List<Order> reportOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public List<Inventory> reportInventoryByWarehouse(Long warehouseId) {
        return inventoryRepository.findByWarehouse_Id(warehouseId);
    }

    @Override
    public long countActiveOrders() {
        return orderRepository.findAll().stream()
                .filter(o -> o.getStatus() != OrderStatus.DELIVERED_COMPLETED && o.getStatus() != OrderStatus.CANCELLED)
                .count();
    }
}
