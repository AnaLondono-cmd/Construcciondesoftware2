package com.nexusmarket.service;

import com.nexusmarket.domain.Invoice;
import com.nexusmarket.domain.Order;

public interface InvoicingService {

    Invoice issueInvoice(Order order);

    void voidInvoice(Long invoiceId);
}
