package com.nexusmarket.domain;

import com.nexusmarket.domain.enums.OrderStatus;
import com.nexusmarket.exception.OrderNotModifiableException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Management domain. Represents the formal commercial commitment; its lifecycle
 * (Cart -> Pending Payment -> Paid -> Shipped -> Delivered/Completed) is the system's core process.
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Buyer buyer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING_PAYMENT;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime updateDate = LocalDateTime.now();

    public Order(Buyer buyer) {
        this.buyer = buyer;
        this.status = OrderStatus.PENDING_PAYMENT;
        this.creationDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
    }

    public void addItem(OrderItem item) {
        item.setOrder(this);
        this.items.add(item);
    }

    public BigDecimal calculateTotal() {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Critical validation: a completed (or cancelled) order can never be modified. */
    public boolean isModifiable() {
        return status != OrderStatus.DELIVERED_COMPLETED && status != OrderStatus.CANCELLED;
    }

    private void assertModifiable() {
        if (!isModifiable()) {
            throw new OrderNotModifiableException(
                    "Order " + id + " is in status " + status + " and can no longer be modified");
        }
    }

    public void confirmPayment() {
        assertModifiable();
        if (status != OrderStatus.PENDING_PAYMENT) {
            throw new OrderNotModifiableException("Only a Pending Payment order can move to Paid");
        }
        this.status = OrderStatus.PAID;
        this.updateDate = LocalDateTime.now();
    }

    public void markAsShipped() {
        assertModifiable();
        if (status != OrderStatus.PAID) {
            throw new OrderNotModifiableException("Only a Paid order can move to Shipped");
        }
        this.status = OrderStatus.SHIPPED;
        this.updateDate = LocalDateTime.now();
    }

    public void complete() {
        assertModifiable();
        if (status != OrderStatus.SHIPPED) {
            throw new OrderNotModifiableException("Only a Shipped order can be completed");
        }
        this.status = OrderStatus.DELIVERED_COMPLETED;
        this.updateDate = LocalDateTime.now();
    }

    public void cancel() {
        if (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED_COMPLETED) {
            throw new OrderNotModifiableException("A shipped or completed order cannot be cancelled");
        }
        this.status = OrderStatus.CANCELLED;
        this.updateDate = LocalDateTime.now();
    }

    public boolean hasPhysicalProducts() {
        return items.stream().anyMatch(i -> i.getProduct().isPhysical());
    }
}
