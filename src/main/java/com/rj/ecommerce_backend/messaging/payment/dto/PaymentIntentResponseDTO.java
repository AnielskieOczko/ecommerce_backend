package com.rj.ecommerce_backend.messaging.payment.dto;

import lombok.Builder;

@Builder
public record PaymentIntentResponseDTO(
        String orderId,
        String paymentIntentId,
        String clientSecret,
        String status,
        String currency,
        Long amount
) {
}
