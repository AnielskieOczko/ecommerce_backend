package com.rj.ecommerce_backend.domain.cart;

import com.rj.ecommerce_backend.domain.cart.dtos.CartDTO;
import com.rj.ecommerce_backend.domain.user.User;

public interface CartService {

    CartDTO getCartForUser(Long userId);
    Cart createEmptyCartForUser(User user);
    CartDTO addItemToCart(Long userId, Long productId, int quantity);
    CartDTO updateCartItemQuantity(Long userId, Long cartItemId, int quantity);
    void removeItemFromCart(Long userId, Long cartItemId);
    void clearCart(Long userId);
}
