package com.nexusmarket.repository;

import com.nexusmarket.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByBuyer_Id(Long buyerId);
}
