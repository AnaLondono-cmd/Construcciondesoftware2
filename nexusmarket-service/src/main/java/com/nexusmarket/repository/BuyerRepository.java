package com.nexusmarket.repository;

import com.nexusmarket.domain.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {
}
