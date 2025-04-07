package com.rj.ecommerce_backend.payment.dto;

import com.rj.ecommerce_backend.order.enums.PaymentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PaymentStatusDTO(
        Long orderId,
        PaymentStatus paymentStatus,
        String paymentTransactionId,
        LocalDateTime lastUpdated
) {
}
