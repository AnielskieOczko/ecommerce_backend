package com.rj.ecommerce_backend.product.dtos;

import java.time.LocalDateTime;

public record ErrorDTO(
        int status,
        String message,
        LocalDateTime timestamp) {
}
