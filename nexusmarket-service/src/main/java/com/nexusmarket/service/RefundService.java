package com.nexusmarket.service;

import com.nexusmarket.domain.Refund;

public interface RefundService {

    Refund process(Long refundId);

    Refund reject(Long refundId);
}
