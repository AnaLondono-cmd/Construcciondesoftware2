package com.nexusmarket.domain;

import com.nexusmarket.exception.InsufficientStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Inventory Management domain. Distributed stock: always linked to one Product and one Warehouse.
 * Invariant: quantities can never go negative. No reservation is allowed on damaged stock.
 */
@Entity
@Table(name = "inventories", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "warehouse_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    private int availableQuantity;
    private int reservedQuantity;
    private int damagedQuantity;
    private boolean markedAsDamaged = false;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL)
    private List<InventoryMovement> movements = new ArrayList<>();

    public Inventory(Product product, Warehouse warehouse, int initialQuantity) {
        this.product = product;
        this.warehouse = warehouse;
        this.availableQuantity = initialQuantity;
        this.reservedQuantity = 0;
        this.damagedQuantity = 0;
    }

    /** No inventory can be reserved if it does not exist or is marked as damaged. */
    public boolean checkAvailability(int quantity) {
        return !markedAsDamaged && availableQuantity >= quantity;
    }

    public void reserve(int quantity) {
        if (!checkAvailability(quantity)) {
            throw new InsufficientStockException(
                    "Not enough availability (or the inventory is damaged) to reserve " + quantity + " units");
        }
        this.availableQuantity -= quantity;
        this.reservedQuantity += quantity;
    }

    public void releaseReservation(int quantity) {
        this.reservedQuantity = Math.max(0, this.reservedQuantity - quantity);
        this.availableQuantity += quantity;
    }

    public void confirmOutbound(int quantity) {
        if (this.reservedQuantity < quantity) {
            throw new InsufficientStockException("There is not enough reserved quantity to confirm the outbound movement");
        }
        this.reservedQuantity -= quantity;
    }

    public void receiveStock(int quantity) {
        this.availableQuantity += quantity;
    }

    public void adjust(int delta) {
        int newQuantity = this.availableQuantity + delta;
        if (newQuantity < 0) {
            throw new InsufficientStockException("This adjustment would leave negative stock, which is not allowed");
        }
        this.availableQuantity = newQuantity;
    }

    public void markAsDamaged(int quantity) {
        if (quantity > this.availableQuantity) {
            throw new InsufficientStockException("Cannot mark as damaged a quantity greater than what is available");
        }
        this.availableQuantity -= quantity;
        this.damagedQuantity += quantity;
        if (this.availableQuantity == 0) {
            this.markedAsDamaged = true;
        }
    }
}
