package com.rj.ecommerce_backend.messaging.payment.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record PaymentVerificationResponseDTO(
        String orderId,
        String paymentIntentId,
        String status,
        String transactionId,
        LocalDateTime processedAt,
        Map<String, String> additionalDetails
) {
}
