package com.rj.ecommerce_backend.messaging.payment.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record PaymentIntentRequestDTO(
        String orderId,
        Long amount,
        String currency,
        String customerEmail,
        Map<String, String> metadata // Additional information like products
) {
}
