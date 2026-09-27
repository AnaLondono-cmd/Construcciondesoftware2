package com.nexusmarket.service.impl;

import com.nexusmarket.domain.Product;
import com.nexusmarket.domain.Seller;
import com.nexusmarket.domain.Variant;
import com.nexusmarket.domain.enums.ProductStatus;
import com.nexusmarket.domain.enums.ProductType;
import com.nexusmarket.exception.ResourceNotFoundException;
import com.nexusmarket.repository.ProductRepository;
import com.nexusmarket.repository.SellerRepository;
import com.nexusmarket.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;

    public ProductServiceImpl(ProductRepository productRepository, SellerRepository sellerRepository) {
        this.productRepository = productRepository;
        this.sellerRepository = sellerRepository;
    }

    @Override
    @Transactional
    public Product registerProduct(Long sellerId, String name, String description,
                                    ProductType productType, BigDecimal price) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found: " + sellerId));
        Product product = new Product(name, description, productType, price, seller);
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Variant addVariant(Long productId, String attribute, String value, String sku) {
        Product product = getProduct(productId);
        Variant variant = new Variant(attribute, value, sku);
        product.addVariant(variant);
        productRepository.save(product);
        return variant;
    }

    @Override
    @Transactional
    public void publishProduct(Long productId) {
        Product product = getProduct(productId);
        product.publish();
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void suspendProduct(Long productId) {
        Product product = getProduct(productId);
        product.suspend();
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void discontinueProduct(Long productId) {
        Product product = getProduct(productId);
        product.discontinue();
        productRepository.save(product);
    }

    @Override
    public List<Product> listPublicCatalog() {
        return productRepository.findByStatus(ProductStatus.PUBLISHED);
    }

    @Override
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
    }
}
