package com.rj.ecommerce_backend.messaging.payment.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response DTO for Stripe Checkout Session operations.
 * Provides a clear contract between the payment service and the main application.
 */
@Builder
public record CheckoutSessionResponseDTO(
        // Session identification
        String sessionId,           // Stripe's session ID
        String orderId,             // Your system's order ID

        // Session status information
        String status,              // Overall status (CHECKOUT_COMPLETED, CHECKOUT_EXPIRED, etc.)
        String paymentStatus,       // Stripe's payment status (paid, unpaid, etc.)

        // URLs
        String checkoutUrl,         // URL for the checkout page

        // Financial information
        String currency,            // Currency code (USD, EUR, etc.)
        Long amountTotal,           // Total amount in smallest currency unit (cents, pence, etc.)

        // Customer information
        String customerEmail,       // Customer's email address

        // Timestamps
        LocalDateTime processedAt,  // When this response was created
        LocalDateTime expiresAt,    // When the session expires (if applicable)

        // Additional details for flexibility
        Map<String, String> additionalDetails
) {
}

