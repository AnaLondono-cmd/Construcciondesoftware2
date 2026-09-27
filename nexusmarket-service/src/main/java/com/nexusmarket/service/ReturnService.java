package com.nexusmarket.service;

import com.nexusmarket.domain.Refund;
import com.nexusmarket.domain.ReturnRequest;

/** Application service for the Returns and Refunds domain. */
public interface ReturnService {

    /** Use case: request a return. Only allowed on an already delivered/completed order. */
    ReturnRequest requestReturn(Long orderId, String reason);

    /** Approving a return re-stocks the physical merchandise into the origin warehouse. */
    ReturnRequest approve(Long returnRequestId, Long originWarehouseId);

    ReturnRequest reject(Long returnRequestId);

    /** Use case: refund management. Only allowed on an approved return. */
    Refund generateRefund(Long returnRequestId);

    ReturnRequest getReturnRequest(Long returnRequestId);
}
