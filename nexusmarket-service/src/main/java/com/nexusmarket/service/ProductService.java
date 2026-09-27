package com.nexusmarket.service;

import com.nexusmarket.domain.Product;
import com.nexusmarket.domain.Variant;
import com.nexusmarket.domain.enums.ProductType;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    Product registerProduct(Long sellerId, String name, String description, ProductType productType, BigDecimal price);

    Variant addVariant(Long productId, String attribute, String value, String sku);

    void publishProduct(Long productId);

    void suspendProduct(Long productId);

    void discontinueProduct(Long productId);

    List<Product> listPublicCatalog();

    Product getProduct(Long productId);
}
