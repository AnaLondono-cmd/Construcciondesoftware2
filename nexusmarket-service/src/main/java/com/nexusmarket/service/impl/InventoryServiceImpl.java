package com.nexusmarket.service.impl;

import com.nexusmarket.domain.Inventory;
import com.nexusmarket.domain.InventoryMovement;
import com.nexusmarket.domain.Product;
import com.nexusmarket.domain.Warehouse;
import com.nexusmarket.domain.enums.MovementType;
import com.nexusmarket.exception.ResourceNotFoundException;
import com.nexusmarket.repository.InventoryRepository;
import com.nexusmarket.repository.ProductRepository;
import com.nexusmarket.repository.WarehouseRepository;
import com.nexusmarket.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository,
                                 ProductRepository productRepository,
                                 WarehouseRepository warehouseRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    @Transactional
    public Inventory registerInitialStock(Long productId, Long warehouseId, int initialQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + warehouseId));

        Inventory inventory = inventoryRepository
                .findByProduct_IdAndWarehouse_Id(productId, warehouseId)
                .orElseGet(() -> new Inventory(product, warehouse, 0));

        inventory.receiveStock(initialQuantity);
        Inventory saved = inventoryRepository.save(inventory);
        recordMovement(saved, MovementType.INBOUND, initialQuantity);
        return saved;
    }

    @Override
    @Transactional
    public void reserve(Long inventoryId, int quantity) {
        Inventory inventory = getInventory(inventoryId);
        inventory.reserve(quantity);
        inventoryRepository.save(inventory);
        recordMovement(inventory, MovementType.RESERVATION, quantity);
    }

    @Override
    @Transactional
    public void releaseReservation(Long inventoryId, int quantity) {
        Inventory inventory = getInventory(inventoryId);
        inventory.releaseReservation(quantity);
        inventoryRepository.save(inventory);
        recordMovement(inventory, MovementType.RESERVATION_RELEASE, quantity);
    }

    @Override
    @Transactional
    public void confirmSaleOutbound(Long inventoryId, int quantity) {
        Inventory inventory = getInventory(inventoryId);
        inventory.confirmOutbound(quantity);
        inventoryRepository.save(inventory);
        recordMovement(inventory, MovementType.SALE_OUTBOUND, quantity);
    }

    @Override
    @Transactional
    public void adjust(Long inventoryId, int delta, String reason) {
        Inventory inventory = getInventory(inventoryId);
        inventory.adjust(delta);
        inventoryRepository.save(inventory);
        recordMovement(inventory, MovementType.ADJUSTMENT, delta);
    }

    @Override
    @Transactional
    public void registerReturnToStock(Long inventoryId, int quantity) {
        Inventory inventory = getInventory(inventoryId);
        inventory.receiveStock(quantity);
        inventoryRepository.save(inventory);
        recordMovement(inventory, MovementType.RETURN, quantity);
    }

    private void recordMovement(Inventory inventory, MovementType type, int quantity) {
        inventory.getMovements().add(new InventoryMovement(inventory, type, quantity));
        inventoryRepository.save(inventory);
    }

    @Override
    public List<Inventory> listByWarehouse(Long warehouseId) {
        return inventoryRepository.findByWarehouse_Id(warehouseId);
    }

    @Override
    public List<Inventory> listByProduct(Long productId) {
        return inventoryRepository.findByProduct_Id(productId);
    }

    @Override
    public Inventory getInventory(Long inventoryId) {
        return inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found: " + inventoryId));
    }

    @Override
    public Inventory getByProductAndWarehouse(Long productId, Long warehouseId) {
        return inventoryRepository.findByProduct_IdAndWarehouse_Id(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No inventory exists for product " + productId + " in warehouse " + warehouseId));
    }
}
