package com.rj.ecommerce_backend.payment.dto;

import com.rj.ecommerce_backend.order.enums.PaymentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CheckoutSessionDTO(
        Long orderId,
        String sessionId,
        String sessionUrl,
        LocalDateTime expiresAt,
        PaymentStatus paymentStatus
) {
}
