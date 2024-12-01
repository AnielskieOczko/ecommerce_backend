package com.rj.ecommerce_backend.domain.cart;

import com.rj.ecommerce_backend.domain.cart.dtos.CartDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Override
    public CartDTO getCartForUser(Long userId) {
        return null;
    }

    @Override
    public CartDTO addItemToCart(Long userId, Long productId, int quantity) {
        return null;
    }

    @Override
    public CartDTO updateCartItemQuantity(Long userId, Long cartItemId, int quantity) {
        return null;
    }

    @Override
    public void removeItemFromCart(Long userId, Long cartItemId) {

    }

    @Override
    public void clearCart(Long userId) {

    }
}
