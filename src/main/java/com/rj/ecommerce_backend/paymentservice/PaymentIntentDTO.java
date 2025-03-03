package com.rj.ecommerce_backend.paymentservice;

public record PaymentIntentDTO(
        String id,
        String clientSecret,
        Long amount,
        String currency,
        String status
) {
}
