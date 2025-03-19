package com.rj.ecommerce_backend.messaging.payment.dto;

import lombok.Builder;

@Builder
public record PaymentVerificationRequestDTO(
        String orderId,
        String paymentIntentId
) {
}
