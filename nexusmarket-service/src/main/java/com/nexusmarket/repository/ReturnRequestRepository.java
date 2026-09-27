package com.nexusmarket.repository;

import com.nexusmarket.domain.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {
    List<ReturnRequest> findByOrder_Id(Long orderId);
}
