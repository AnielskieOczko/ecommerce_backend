package com.rj.ecommerce_backend.cart.exceptions;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String userId) {
        super("Cart not found for {}" + userId);
    }
}
