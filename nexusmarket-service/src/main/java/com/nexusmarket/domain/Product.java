package com.nexusmarket.domain;

import com.nexusmarket.domain.enums.ProductStatus;
import com.nexusmarket.domain.enums.ProductType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.SUSPENDED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Variant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<Inventory> inventories = new ArrayList<>();

    public Product(String name, String description, ProductType productType, BigDecimal price, Seller seller) {
        this.name = name;
        this.description = description;
        this.productType = productType;
        this.price = price;
        this.seller = seller;
        this.status = ProductStatus.SUSPENDED;
    }

    public void publish() {
        this.status = ProductStatus.PUBLISHED;
    }

    public void suspend() {
        this.status = ProductStatus.SUSPENDED;
    }

    public void discontinue() {
        this.status = ProductStatus.DISCONTINUED;
    }

    public void addVariant(Variant variant) {
        variant.setProduct(this);
        this.variants.add(variant);
    }

    public boolean isPhysical() {
        return this.productType == ProductType.PHYSICAL;
    }

    public boolean isPublished() {
        return this.status == ProductStatus.PUBLISHED;
    }
}
