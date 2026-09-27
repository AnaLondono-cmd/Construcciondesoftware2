package com.nexusmarket.repository;

import com.nexusmarket.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProduct_IdAndWarehouse_Id(Long productId, Long warehouseId);
    List<Inventory> findByProduct_Id(Long productId);
    List<Inventory> findByWarehouse_Id(Long warehouseId);
}
