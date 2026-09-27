package com.nexusmarket.domain;

import com.nexusmarket.domain.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Refunds domain. Monetary compensation tied to an approved ReturnRequest. */
@Entity
@Table(name = "refunds")
@Getter
@Setter
@NoArgsConstructor
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_request_id", nullable = false, unique = true)
    private ReturnRequest returnRequest;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private RefundStatus status = RefundStatus.PENDING;

    private LocalDateTime processingDate;

    public Refund(ReturnRequest returnRequest, BigDecimal amount) {
        this.returnRequest = returnRequest;
        this.amount = amount;
        this.status = RefundStatus.PENDING;
    }

    public void process() {
        this.status = RefundStatus.PROCESSED;
        this.processingDate = LocalDateTime.now();
    }

    public void reject() {
        this.status = RefundStatus.REJECTED;
        this.processingDate = LocalDateTime.now();
    }
}
