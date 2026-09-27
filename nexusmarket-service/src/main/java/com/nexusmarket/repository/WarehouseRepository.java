package com.nexusmarket.repository;

import com.nexusmarket.domain.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    List<Warehouse> findByOwner_Id(Long sellerId);
}
