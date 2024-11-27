package com.rj.ecommerce_backend.domain.order.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO(Long id, Long userId, List<OrderItemDTO> orderItems, BigDecimal totalPrice,
                       AddressDTO shippingAddress, String paymentMethod, String paymentTransactionId,
                       LocalDateTime orderDate, String orderStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
