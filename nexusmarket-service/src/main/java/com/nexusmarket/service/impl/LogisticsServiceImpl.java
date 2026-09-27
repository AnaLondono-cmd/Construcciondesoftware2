package com.nexusmarket.service.impl;

import com.nexusmarket.domain.LogisticsOperator;
import com.nexusmarket.domain.Order;
import com.nexusmarket.domain.Shipment;
import com.nexusmarket.domain.enums.ShipmentStatus;
import com.nexusmarket.exception.ResourceNotFoundException;
import com.nexusmarket.repository.LogisticsOperatorRepository;
import com.nexusmarket.repository.ShipmentRepository;
import com.nexusmarket.service.LogisticsService;
import com.nexusmarket.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class LogisticsServiceImpl implements LogisticsService {

    private final ShipmentRepository shipmentRepository;
    private final LogisticsOperatorRepository logisticsOperatorRepository;
    private final OrderService orderService;

    public LogisticsServiceImpl(ShipmentRepository shipmentRepository,
                                 LogisticsOperatorRepository logisticsOperatorRepository,
                                 OrderService orderService) {
        this.shipmentRepository = shipmentRepository;
        this.logisticsOperatorRepository = logisticsOperatorRepository;
        this.orderService = orderService;
    }

    @Override
    @Transactional
    public Shipment registerDispatch(Long orderId, Long logisticsOperatorId, Long dispatchWarehouseId) {
        Order order = orderService.ship(orderId, dispatchWarehouseId);
        LogisticsOperator operator = logisticsOperatorRepository.findById(logisticsOperatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Logistics operator not found: " + logisticsOperatorId));

        Shipment shipment = new Shipment(order, operator, generateTrackingNumber());
        return shipmentRepository.save(shipment);
    }

    @Override
    @Transactional
    public Shipment updateShipmentStatus(Long shipmentId, ShipmentStatus newStatus) {
        Shipment shipment = getShipment(shipmentId);
        shipment.updateStatus(newStatus);
        return shipmentRepository.save(shipment);
    }

    @Override
    @Transactional
    public Shipment confirmDelivery(Long shipmentId) {
        Shipment shipment = getShipment(shipmentId);
        shipment.confirmDelivery();
        shipmentRepository.save(shipment);
        orderService.complete(shipment.getOrder().getId());
        return shipment;
    }

    private String generateTrackingNumber() {
        return "NM-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }

    @Override
    public Shipment getShipment(Long shipmentId) {
        return shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found: " + shipmentId));
    }
}
