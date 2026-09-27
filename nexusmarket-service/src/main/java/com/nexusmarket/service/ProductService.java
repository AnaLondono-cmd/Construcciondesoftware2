package com.nexusmarket.service;

import com.nexusmarket.domain.Product;
import com.nexusmarket.domain.Variant;
import com.nexusmarket.domain.enums.ProductType;

import java.math.BigDecimal;
import java.util.List;

/** Application service for the Catalog Management domain. Only the owning Seller manages its own catalog. */
public interface ProductService {

    /** Use case: the seller registers products and defines their features (Business Flow step 2). */
    Product registerProduct(Long sellerId, String name, String description, ProductType productType, BigDecimal price);

    Variant addVariant(Long productId, String attribute, String value, String sku);

    /** Use case: publication — products become visible in the public catalog (Business Flow step 4). */
    void publishProduct(Long productId);

    void suspendProduct(Long productId);

    void discontinueProduct(Long productId);

    List<Product> listPublicCatalog();

    Product getProduct(Long productId);
}
