package com.nexusmarket.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Invoicing domain. Commercial information tied to the sale, generated once payment is confirmed. */
@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    private LocalDateTime issueDate;
    private BigDecimal totalAmount;
    private BigDecimal taxes;
    private boolean voided = false;

    public Invoice(Order order, BigDecimal totalAmount, BigDecimal taxes) {
        this.order = order;
        this.totalAmount = totalAmount;
        this.taxes = taxes;
        this.issueDate = LocalDateTime.now();
    }

    public void voidInvoice() {
        this.voided = true;
    }
}
