package com.nexusmarket.service.impl;

import com.nexusmarket.domain.*;
import com.nexusmarket.exception.NexusMarketException;
import com.nexusmarket.exception.ResourceNotFoundException;
import com.nexusmarket.repository.BuyerRepository;
import com.nexusmarket.repository.CartRepository;
import com.nexusmarket.repository.ProductRepository;
import com.nexusmarket.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final BuyerRepository buyerRepository;
    private final ProductRepository productRepository;

    public CartServiceImpl(CartRepository cartRepository,
                            BuyerRepository buyerRepository,
                            ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.buyerRepository = buyerRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Cart getOrCreateCart(Long buyerId) {
        return cartRepository.findByBuyer_Id(buyerId)
                .orElseGet(() -> {
                    Buyer buyer = buyerRepository.findById(buyerId)
                            .orElseThrow(() -> new ResourceNotFoundException("Buyer not found: " + buyerId));
                    return cartRepository.save(new Cart(buyer));
                });
    }

    @Override
    @Transactional
    public Cart addProduct(Long buyerId, Long productId, Long variantId, int quantity) {
        Cart cart = getOrCreateCart(buyerId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        if (!product.isPublished()) {
            throw new NexusMarketException("Product " + productId + " is not published in the catalog");
        }

        Variant variant = null;
        if (variantId != null) {
            variant = product.getVariants().stream()
                    .filter(v -> v.getId().equals(variantId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Variant not found: " + variantId));
        }

        CartItem item = new CartItem(product, variant, quantity, product.getPrice());
        cart.addItem(item);
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart removeProduct(Long buyerId, Long cartItemId) {
        Cart cart = getOrCreateCart(buyerId);
        cart.getItems().removeIf(i -> i.getId().equals(cartItemId));
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long buyerId) {
        Cart cart = getOrCreateCart(buyerId);
        cart.clear();
        cartRepository.save(cart);
    }
}
