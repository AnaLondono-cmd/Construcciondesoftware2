package com.nexusmarket.service;

import com.nexusmarket.domain.Cart;

/** Application service for the shopping cart use case (Order Management domain, step 1). */
public interface CartService {

    Cart getOrCreateCart(Long buyerId);

    /** Use case: the buyer selects products through the cart (Business Flow step 5). */
    Cart addProduct(Long buyerId, Long productId, Long variantId, int quantity);

    Cart removeProduct(Long buyerId, Long cartItemId);

    void clearCart(Long buyerId);
}
