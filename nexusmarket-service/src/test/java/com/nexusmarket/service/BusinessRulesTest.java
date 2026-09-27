package com.nexusmarket.service;

import com.nexusmarket.domain.*;
import com.nexusmarket.domain.enums.ProductType;
import com.nexusmarket.domain.enums.WarehouseType;
import com.nexusmarket.exception.InsufficientStockException;
import com.nexusmarket.exception.OrderNotModifiableException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the domain invariants, with no need to bootstrap the Spring context:
 *  - Inventory never goes negative / cannot be reserved when marked as damaged.
 *  - A completed order can no longer be modified.
 */
class BusinessRulesTest {

    @Test
    void cannotReserveMoreStockThanAvailable() {
        Product product = new Product("Backpack", "Urban backpack", ProductType.PHYSICAL, new BigDecimal("80000"), null);
        Warehouse warehouse = new Warehouse("Test Warehouse", WarehouseType.MARKETPLACE, null, null);
        Inventory inventory = new Inventory(product, warehouse, 5);

        assertThrows(InsufficientStockException.class, () -> inventory.reserve(10));
        assertEquals(5, inventory.getAvailableQuantity());
    }

    @Test
    void cannotReserveInventoryMarkedAsDamaged() {
        Product product = new Product("Backpack", "Urban backpack", ProductType.PHYSICAL, new BigDecimal("80000"), null);
        Warehouse warehouse = new Warehouse("Test Warehouse", WarehouseType.MARKETPLACE, null, null);
        Inventory inventory = new Inventory(product, warehouse, 3);

        inventory.markAsDamaged(3);
        assertThrows(InsufficientStockException.class, () -> inventory.reserve(1));
    }

    @Test
    void anAdjustmentThatWouldLeaveNegativeStockIsRejected() {
        Product product = new Product("Backpack", "Urban backpack", ProductType.PHYSICAL, new BigDecimal("80000"), null);
        Warehouse warehouse = new Warehouse("Test Warehouse", WarehouseType.MARKETPLACE, null, null);
        Inventory inventory = new Inventory(product, warehouse, 2);

        assertThrows(InsufficientStockException.class, () -> inventory.adjust(-5));
    }

    @Test
    void aCompletedOrderCannotBeModified() {
        Buyer buyer = new Buyer("Test Buyer", "test@mail.com",
                new Address("Street 1", "City", "000000", "Colombia"));
        Order order = new Order(buyer);

        order.confirmPayment();
        order.markAsShipped();
        order.complete();

        assertFalse(order.isModifiable());
        assertThrows(OrderNotModifiableException.class, order::confirmPayment);
        assertThrows(OrderNotModifiableException.class, order::cancel);
    }

    @Test
    void aShippedOrderCannotBeCancelled() {
        Buyer buyer = new Buyer("Test Buyer", "test@mail.com",
                new Address("Street 1", "City", "000000", "Colombia"));
        Order order = new Order(buyer);

        order.confirmPayment();
        order.markAsShipped();

        assertThrows(OrderNotModifiableException.class, order::cancel);
    }
}
