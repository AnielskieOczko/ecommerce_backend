package com.rj.ecommerce_backend.messaging.email.contract.v1.customer;

import com.rj.ecommerce_backend.messaging.email.contract.v1.EcommerceEmailRequest;
import com.rj.ecommerce_backend.messaging.email.contract.v1.EmailTemplate;
import lombok.Builder;
import lombok.NonNull;
import lombok.With;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object for welcome emails to new customers
 */
@Builder
@With
public record WelcomeEmailRequestDTO(
        @NonNull String messageId,
        @NonNull String version,
        @NonNull String to,
        String subject,
        @NonNull String customerName,
        String couponCode,
        Map<String, Object> additionalData,
        LocalDateTime timestamp
) implements EcommerceEmailRequest {

    /**
     * Validates and creates a new WelcomeEmailRequestDTO.
     */
    public WelcomeEmailRequestDTO {
        if (messageId.isBlank()) {
            throw new IllegalArgumentException("Message ID cannot be blank");
        }
        if (version.isBlank()) {
            throw new IllegalArgumentException("Version cannot be blank");
        }
        if (to.isBlank()) {
            throw new IllegalArgumentException("Recipient email cannot be blank");
        }
        if (customerName.isBlank()) {
            throw new IllegalArgumentException("Customer name cannot be blank");
        }

        // Generate subject if not provided
        subject = subject != null && !subject.isBlank() ? subject : "Welcome to Our Store!";

        // Set default timestamp if not provided
        timestamp = timestamp != null ? timestamp : LocalDateTime.now();

        // Ensure additionalData is not null
        additionalData = additionalData != null ? additionalData : Map.of();
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
        return EmailTemplate.CUSTOMER_WELCOME;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public Map<String, Object> getTemplateData() {
        Map<String, Object> data = new HashMap<>();
        data.put("customerName", customerName);
        data.put("couponCode", couponCode);

        // Add any additional data
        if (additionalData != null) {
            data.putAll(additionalData);
        }

        return data;
    }

    /**
     * Creates a builder with default values.
     */
    public static WelcomeEmailRequestDTOBuilder defaultBuilder() {
        return builder()
                .messageId(UUID.randomUUID().toString())
                .version("1.0")
                .timestamp(LocalDateTime.now());
    }
}
