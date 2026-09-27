package com.nexusmarket.service;

import com.nexusmarket.domain.Refund;

/** Application service dedicated to Refund processing. */
public interface RefundService {

    Refund process(Long refundId);

    Refund reject(Long refundId);
}
