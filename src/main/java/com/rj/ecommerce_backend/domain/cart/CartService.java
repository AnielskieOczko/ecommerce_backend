package com.rj.ecommerce_backend.domain.cart;

import com.rj.ecommerce_backend.domain.cart.dtos.CartDTO;

public interface CartService {

    CartDTO getCartForUser(Long userId);
    CartDTO addItemToCart(Long userId, Long productId, int quantity);
    CartDTO updateCartItemQuantity(Long userId, Long cartItemId, int quantity);
    void removeItemFromCart(Long userId, Long cartItemId);
    void clearCart(Long userId);
}
