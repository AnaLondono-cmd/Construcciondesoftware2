package com.nexusmarket.service.impl;

import com.nexusmarket.domain.*;
import com.nexusmarket.domain.enums.OrderStatus;
import com.nexusmarket.domain.enums.ReturnStatus;
import com.nexusmarket.exception.NexusMarketException;
import com.nexusmarket.exception.ResourceNotFoundException;
import com.nexusmarket.repository.InventoryRepository;
import com.nexusmarket.repository.OrderRepository;
import com.nexusmarket.repository.RefundRepository;
import com.nexusmarket.repository.ReturnRequestRepository;
import com.nexusmarket.service.InventoryService;
import com.nexusmarket.service.ReturnService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReturnServiceImpl implements ReturnService {

    private final ReturnRequestRepository returnRequestRepository;
    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;

    public ReturnServiceImpl(ReturnRequestRepository returnRequestRepository,
                              RefundRepository refundRepository,
                              OrderRepository orderRepository,
                              InventoryRepository inventoryRepository,
                              InventoryService inventoryService) {
        this.returnRequestRepository = returnRequestRepository;
        this.refundRepository = refundRepository;
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryService = inventoryService;
    }

    @Override
    @Transactional
    public ReturnRequest requestReturn(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        if (order.getStatus() != OrderStatus.DELIVERED_COMPLETED) {
            throw new NexusMarketException("A return can only be requested for a delivered/completed order");
        }
        ReturnRequest returnRequest = new ReturnRequest(order, reason);
        return returnRequestRepository.save(returnRequest);
    }

    @Override
    @Transactional
    public ReturnRequest approve(Long returnRequestId, Long originWarehouseId) {
        ReturnRequest returnRequest = getReturnRequest(returnRequestId);
        returnRequest.approve();

        for (OrderItem item : returnRequest.getOrder().getItems()) {
            if (item.getProduct().isPhysical()) {
                inventoryRepository
                        .findByProduct_IdAndWarehouse_Id(item.getProduct().getId(), originWarehouseId)
                        .ifPresent(inv -> inventoryService.registerReturnToStock(inv.getId(), item.getQuantity()));
            }
        }
        return returnRequestRepository.save(returnRequest);
    }

    @Override
    @Transactional
    public ReturnRequest reject(Long returnRequestId) {
        ReturnRequest returnRequest = getReturnRequest(returnRequestId);
        returnRequest.reject();
        return returnRequestRepository.save(returnRequest);
    }

    @Override
    @Transactional
    public Refund generateRefund(Long returnRequestId) {
        ReturnRequest returnRequest = getReturnRequest(returnRequestId);
        if (returnRequest.getStatus() != ReturnStatus.APPROVED) {
            throw new NexusMarketException("Only an approved return request can be refunded");
        }
        Refund refund = new Refund(returnRequest, returnRequest.getOrder().calculateTotal());
        return refundRepository.save(refund);
    }

    @Override
    public ReturnRequest getReturnRequest(Long returnRequestId) {
        return returnRequestRepository.findById(returnRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Return request not found: " + returnRequestId));
    }
}
