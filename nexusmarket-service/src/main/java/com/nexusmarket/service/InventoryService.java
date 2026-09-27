package com.nexusmarket.service;

import com.nexusmarket.domain.Inventory;

import java.util.List;

/**
 * Application service for the Inventory Management domain.
 * Centralizes the rules: inventory is distributed and always linked to a Product + Warehouse,
 * stock can never go negative, and reservations are never allowed on damaged stock.
 */
public interface InventoryService {

    /** Use case: registering initial stock in the associated warehouses (Business Flow step 3). */
    Inventory registerInitialStock(Long productId, Long warehouseId, int initialQuantity);

    void reserve(Long inventoryId, int quantity);

    void releaseReservation(Long inventoryId, int quantity);

    void confirmSaleOutbound(Long inventoryId, int quantity);

    void adjust(Long inventoryId, int delta, String reason);

    void registerReturnToStock(Long inventoryId, int quantity);

    List<Inventory> listByWarehouse(Long warehouseId);

    List<Inventory> listByProduct(Long productId);

    Inventory getInventory(Long inventoryId);

    Inventory getByProductAndWarehouse(Long productId, Long warehouseId);
}
