package com.nexusmarket.service;

import com.nexusmarket.domain.Inventory;

import java.util.List;


public interface InventoryService {

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
