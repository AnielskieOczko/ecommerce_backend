package com.rj.ecommerce_backend.messaging.email.contract.v1.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rj.ecommerce_backend.messaging.email.contract.v1.EcommerceEmailRequest;
import com.rj.ecommerce_backend.messaging.email.contract.v1.EmailTemplate;
import com.rj.ecommerce_backend.messaging.email.contract.v1.common.MoneyDTO;
import lombok.Builder;
import lombok.NonNull;
import lombok.With;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object for payment-related email requests
 */
@Builder
@With
public record PaymentEmailRequestDTO(
        @NonNull String messageId,
        @NonNull String version,
        @NonNull String to,
        String subject,
        @NonNull EmailTemplate template,
        @NonNull String orderId,
        String paymentId,
        @NonNull String paymentStatus,
        MoneyDTO paymentAmount,
        Map<String, Object> additionalData,
        LocalDateTime timestamp
) implements EcommerceEmailRequest {

    /**
     * Validates and creates a new PaymentEmailRequestDTO.
     */
    public PaymentEmailRequestDTO {
        if (messageId.isBlank()) {
            throw new IllegalArgumentException("Message ID cannot be blank");
        }
        if (version.isBlank()) {
            throw new IllegalArgumentException("Version cannot be blank");
        }
        if (to.isBlank()) {
            throw new IllegalArgumentException("Recipient email cannot be blank");
        }
        if (orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID cannot be blank");
        }
        if (paymentStatus.isBlank()) {
            throw new IllegalArgumentException("Payment status cannot be blank");
        }

        // Generate subject if not provided
        subject = subject != null && !subject.isBlank() ? subject : generateSubject(template, orderId);

        // Set default timestamp if not provided
        timestamp = timestamp != null ? timestamp : LocalDateTime.now();

        // Ensure additionalData is not null
        additionalData = additionalData != null ? additionalData : Map.of();
    }

    /**
     * Generates a subject line based on the template and order number.
     */
    private static String generateSubject(EmailTemplate template, String orderId) {
        return switch (template) {
            case PAYMENT_CONFIRMATION -> "Payment Confirmed - Order #" + orderId;
            case PAYMENT_FAILED -> "Payment Failed - Order #" + orderId;
            case PAYMENT_ERROR_ADMIN -> "Payment Processing Error - Order #" + orderId;
            case PAYMENT_ERROR_CUSTOMER -> "Payment Processing Update - Order #" + orderId;
            default -> "Payment Information - Order #" + orderId;
        };
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getTo() {
        return to;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public EmailTemplate getTemplate() {
        return template;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    @JsonIgnore
    public Map<String, Object> getTemplateData() {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("paymentId", paymentId);
        data.put("paymentStatus", paymentStatus);
        data.put("paymentAmount", paymentAmount);

        // Add any additional data
        if (additionalData != null) {
            data.putAll(additionalData);
        }

        return data;
    }

    /**
     * Creates a builder with default values.
     */
    public static PaymentEmailRequestDTOBuilder defaultBuilder() {
        return builder()
                .messageId(UUID.randomUUID().toString())
                .version("1.0")
                .timestamp(LocalDateTime.now());
    }
}
