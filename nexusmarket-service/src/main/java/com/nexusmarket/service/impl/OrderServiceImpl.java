package com.nexusmarket.service.impl;

import com.nexusmarket.domain.*;
import com.nexusmarket.exception.NexusMarketException;
import com.nexusmarket.exception.ResourceNotFoundException;
import com.nexusmarket.repository.CartRepository;
import com.nexusmarket.repository.InventoryRepository;
import com.nexusmarket.repository.OrderRepository;
import com.nexusmarket.service.InventoryService;
import com.nexusmarket.service.InvoicingService;
import com.nexusmarket.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;
    private final InvoicingService invoicingService;

    public OrderServiceImpl(OrderRepository orderRepository,
                             CartRepository cartRepository,
                             InventoryRepository inventoryRepository,
                             InventoryService inventoryService,
                             InvoicingService invoicingService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryService = inventoryService;
        this.invoicingService = invoicingService;
    }

   
    @Override
    @Transactional
    public Order confirmOrder(Long buyerId, Long dispatchWarehouseId) {
        Cart cart = cartRepository.findByBuyer_Id(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("The buyer has no active cart"));

        if (cart.isEmpty()) {
            throw new NexusMarketException("An order cannot be confirmed with an empty cart");
        }
        if (!cart.getBuyer().canPurchase()) {
            throw new NexusMarketException("The buyer is not enabled to make purchases");
        }

        Order order = new Order(cart.getBuyer());

        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            order.addItem(new OrderItem(product, item.getVariant(), item.getQuantity(), item.getUnitPrice()));

            if (product.isPhysical()) {
                Inventory inventory = inventoryRepository
                        .findByProduct_IdAndWarehouse_Id(product.getId(), dispatchWarehouseId)
                        .orElseThrow(() -> new NexusMarketException(
                                "No inventory of product " + product.getId() + " in warehouse " + dispatchWarehouseId));
                inventoryService.reserve(inventory.getId(), item.getQuantity());
            }
        }

        Order saved = orderRepository.save(order);
        cart.clear();
        cartRepository.save(cart);
        return saved;
    }

    @Override
    @Transactional
    public Order confirmPayment(Long orderId) {
        Order order = getOrder(orderId);
        order.confirmPayment();
        orderRepository.save(order);
        invoicingService.issueInvoice(order);
        return order;
    }

    @Override
    @Transactional
    public Order ship(Long orderId, Long dispatchWarehouseId) {
        Order order = getOrder(orderId);
        order.markAsShipped();

        for (OrderItem item : order.getItems()) {
            if (item.getProduct().isPhysical()) {
                Inventory inventory = inventoryRepository
                        .findByProduct_IdAndWarehouse_Id(item.getProduct().getId(), dispatchWarehouseId)
                        .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for dispatch"));
                inventoryService.confirmSaleOutbound(inventory.getId(), item.getQuantity());
            }
        }
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order complete(Long orderId) {
        Order order = getOrder(orderId);
        order.complete();
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order cancel(Long orderId, Long dispatchWarehouseId) {
        Order order = getOrder(orderId);
        order.cancel();
        for (OrderItem item : order.getItems()) {
            if (item.getProduct().isPhysical()) {
                inventoryRepository
                        .findByProduct_IdAndWarehouse_Id(item.getProduct().getId(), dispatchWarehouseId)
                        .ifPresent(inv -> inventoryService.releaseReservation(inv.getId(), item.getQuantity()));
            }
        }
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getPurchaseHistory(Long buyerId) {
        return orderRepository.findByBuyer_Id(buyerId);
    }

    @Override
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }
}
