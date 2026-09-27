package com.nexusmarket.service.impl;

import com.nexusmarket.domain.Invoice;
import com.nexusmarket.domain.Order;
import com.nexusmarket.exception.ResourceNotFoundException;
import com.nexusmarket.repository.InvoiceRepository;
import com.nexusmarket.service.InvoicingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class InvoicingServiceImpl implements InvoicingService {

    private final InvoiceRepository invoiceRepository;
    private final BigDecimal taxRate;

    public InvoicingServiceImpl(InvoiceRepository invoiceRepository,
                                 @Value("${nexusmarket.invoicing.tax-rate:0.19}") double taxRate) {
        this.invoiceRepository = invoiceRepository;
        this.taxRate = BigDecimal.valueOf(taxRate);
    }

    @Override
    @Transactional
    public Invoice issueInvoice(Order order) {
        BigDecimal subtotal = order.calculateTotal();
        BigDecimal taxes = subtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(taxes);
        Invoice invoice = new Invoice(order, total, taxes);
        return invoiceRepository.save(invoice);
    }

    @Override
    @Transactional
    public void voidInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));
        invoice.voidInvoice();
        invoiceRepository.save(invoice);
    }
}
