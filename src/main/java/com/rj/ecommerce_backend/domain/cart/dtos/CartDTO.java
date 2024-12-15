package com.rj.ecommerce_backend.domain.cart.dtos;

import java.time.LocalDateTime;
import java.util.List;

    public record CartDTO(
            Long id,
            Long userId,
            List<CartItemDTO> cartItems,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
    }
