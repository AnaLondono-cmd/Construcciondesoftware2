package com.nexusmarket.repository;

import com.nexusmarket.domain.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByOrder_Id(Long orderId);
}
