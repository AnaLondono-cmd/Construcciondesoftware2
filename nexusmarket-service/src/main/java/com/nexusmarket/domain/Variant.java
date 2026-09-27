package com.nexusmarket.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "variants")
@Getter
@Setter
@NoArgsConstructor
public class Variant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private String attribute;

    @Column(nullable = false)
    private String value;

    @Column(unique = true)
    private String variantSku;

    public Variant(String attribute, String value, String variantSku) {
        this.attribute = attribute;
        this.value = value;
        this.variantSku = variantSku;
    }
}
