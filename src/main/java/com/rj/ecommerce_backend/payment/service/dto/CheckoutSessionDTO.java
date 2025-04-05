package com.rj.ecommerce_backend.payment.service.dto;

import com.rj.ecommerce_backend.order.enums.PaymentStatus;
import lombok.Builder;

@Builder
public record CheckoutSessionDTO(
        Long orderId,
        String sessionId,
        String sessionUrl,
        PaymentStatus paymentStatus
) {
}
