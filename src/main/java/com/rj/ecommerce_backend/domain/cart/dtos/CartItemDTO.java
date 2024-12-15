package com.rj.ecommerce_backend.domain.cart.dtos;

import java.math.BigDecimal;

public record CartItemDTO(
        Long id,
        Long cartId,
        Long productId,
        String productName,
        int quantity,
        BigDecimal price) {
}
