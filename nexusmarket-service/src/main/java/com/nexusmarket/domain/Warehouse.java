package com.nexusmarket.domain;

import com.nexusmarket.domain.enums.WarehouseType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WarehouseType warehouseType;

    @Embedded
    private Address location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller owner;

    @OneToMany(mappedBy = "warehouse")
    private List<Inventory> inventories = new ArrayList<>();

    public Warehouse(String name, WarehouseType warehouseType, Address location, Seller owner) {
        this.name = name;
        this.warehouseType = warehouseType;
        this.location = location;
        this.owner = owner;
    }
}
