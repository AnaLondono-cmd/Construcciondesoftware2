package com.nexusmarket.service;

import com.nexusmarket.domain.Invoice;
import com.nexusmarket.domain.Order;

/** Application service for the Invoicing domain. */
public interface InvoicingService {

    /** Use case: payment is validated and the invoice is issued (Business Flow step 6). */
    Invoice issueInvoice(Order order);

    void voidInvoice(Long invoiceId);
}
