package com.nexusmarket.domain;

import com.nexusmarket.domain.enums.MovementType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_movements")
@Getter
@Setter
@NoArgsConstructor
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType movementType;

    private int quantity;

    private LocalDateTime date = LocalDateTime.now();

    public InventoryMovement(Inventory inventory, MovementType movementType, int quantity) {
        this.inventory = inventory;
        this.movementType = movementType;
        this.quantity = quantity;
        this.date = LocalDateTime.now();
    }
}
