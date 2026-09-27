package com.nexusmarket.service;

import com.nexusmarket.domain.Cart;

public interface CartService {

    Cart getOrCreateCart(Long buyerId);

    Cart addProduct(Long buyerId, Long productId, Long variantId, int quantity);

    Cart removeProduct(Long buyerId, Long cartItemId);

    void clearCart(Long buyerId);
}
