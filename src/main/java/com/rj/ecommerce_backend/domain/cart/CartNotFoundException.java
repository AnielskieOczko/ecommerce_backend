package com.rj.ecommerce_backend.domain.cart;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String userId) {
        super("Cart not found for {}" + userId);
    }
}
