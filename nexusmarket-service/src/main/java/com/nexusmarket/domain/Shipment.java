package com.nexusmarket.domain;

import com.nexusmarket.domain.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Logistics process for physical products: packing, dispatch and transport. */
@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logistics_operator_id", nullable = false)
    private LogisticsOperator logisticsOperator;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status = ShipmentStatus.PREPARING;

    @Column(unique = true)
    private String trackingNumber;

    private LocalDateTime dispatchDate;
    private LocalDateTime deliveryDate;

    public Shipment(Order order, LogisticsOperator logisticsOperator, String trackingNumber) {
        this.order = order;
        this.logisticsOperator = logisticsOperator;
        this.trackingNumber = trackingNumber;
        this.status = ShipmentStatus.PREPARING;
        this.dispatchDate = LocalDateTime.now();
    }

    public void updateStatus(ShipmentStatus newStatus) {
        this.status = newStatus;
    }

    public void confirmDelivery() {
        this.status = ShipmentStatus.DELIVERED;
        this.deliveryDate = LocalDateTime.now();
    }
}
