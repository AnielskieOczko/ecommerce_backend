package com.rj.ecommerce_backend.domain.cart.dtos;

public record CartItemRequest(Long productId, Integer quantity) {
}
