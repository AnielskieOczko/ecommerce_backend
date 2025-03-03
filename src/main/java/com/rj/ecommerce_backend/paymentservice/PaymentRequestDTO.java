package com.rj.ecommerce_backend.paymentservice;

public record PaymentRequestDTO(
        Long amount,
        String currency,
        String orderId,
        String customerEmail
) { }

