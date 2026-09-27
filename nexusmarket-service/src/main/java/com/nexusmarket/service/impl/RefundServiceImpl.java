package com.nexusmarket.service.impl;

import com.nexusmarket.domain.Refund;
import com.nexusmarket.exception.ResourceNotFoundException;
import com.nexusmarket.repository.RefundRepository;
import com.nexusmarket.service.RefundService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefundServiceImpl implements RefundService {

    private final RefundRepository refundRepository;

    public RefundServiceImpl(RefundRepository refundRepository) {
        this.refundRepository = refundRepository;
    }

    @Override
    @Transactional
    public Refund process(Long refundId) {
        Refund refund = get(refundId);
        refund.process();
        return refundRepository.save(refund);
    }

    @Override
    @Transactional
    public Refund reject(Long refundId) {
        Refund refund = get(refundId);
        refund.reject();
        return refundRepository.save(refund);
    }

    private Refund get(Long refundId) {
        return refundRepository.findById(refundId)
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found: " + refundId));
    }
}
