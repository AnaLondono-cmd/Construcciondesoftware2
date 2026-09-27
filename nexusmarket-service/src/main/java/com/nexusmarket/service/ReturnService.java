package com.nexusmarket.service;

import com.nexusmarket.domain.Refund;
import com.nexusmarket.domain.ReturnRequest;

public interface ReturnService {

    ReturnRequest requestReturn(Long orderId, String reason);

    ReturnRequest approve(Long returnRequestId, Long originWarehouseId);

    ReturnRequest reject(Long returnRequestId);

    Refund generateRefund(Long returnRequestId);

    ReturnRequest getReturnRequest(Long returnRequestId);
}
