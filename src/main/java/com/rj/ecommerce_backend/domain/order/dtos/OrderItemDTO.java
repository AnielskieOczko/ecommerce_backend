package com.rj.ecommerce_backend.domain.order.dtos;

import java.math.BigDecimal;

public record OrderItemDTO(Long id, Long orderId, Long productId, String productName, int quantity, BigDecimal price) {
}
