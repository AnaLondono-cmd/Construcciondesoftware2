package com.nexusmarket.domain;

import com.nexusmarket.domain.enums.ReturnStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Returns domain. Only applies to an order that has already been delivered/completed. */
@Entity
@Table(name = "return_requests")
@Getter
@Setter
@NoArgsConstructor
public class ReturnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private String reason;

    @Enumerated(EnumType.STRING)
    private ReturnStatus status = ReturnStatus.REQUESTED;

    private LocalDateTime requestDate = LocalDateTime.now();

    public ReturnRequest(Order order, String reason) {
        this.order = order;
        this.reason = reason;
        this.status = ReturnStatus.REQUESTED;
        this.requestDate = LocalDateTime.now();
    }

    public void approve() {
        this.status = ReturnStatus.APPROVED;
    }

    public void reject() {
        this.status = ReturnStatus.REJECTED;
    }
}
