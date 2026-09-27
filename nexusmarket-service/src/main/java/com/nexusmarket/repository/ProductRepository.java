package com.nexusmarket.repository;

import com.nexusmarket.domain.Product;
import com.nexusmarket.domain.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySeller_Id(Long sellerId);
    List<Product> findByStatus(ProductStatus status);
}
