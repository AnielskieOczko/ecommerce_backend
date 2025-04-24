package com.rj.ecommerce_backend.messaging.email.contract.v1.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rj.ecommerce_backend.messaging.email.contract.v1.EcommerceEmailRequest;
import com.rj.ecommerce_backend.messaging.email.contract.v1.EmailRequestUtils;
import com.rj.ecommerce_backend.messaging.email.contract.v1.EmailTemplate;
import com.rj.ecommerce_backend.messaging.email.contract.v1.common.MoneyDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
        @NonNull @NotBlank String messageId,
        @NonNull @NotBlank String version,
        @NonNull @NotBlank String to,
        String subject,
        @NonNull @NotBlank EmailTemplate template,
        @NonNull @NotBlank String orderId,
        String paymentId,
        @NonNull @NotBlank String paymentStatus,
        @NotNull MoneyDTO paymentAmount,
        Map<String, Object> additionalData,
        LocalDateTime timestamp
) implements EcommerceEmailRequest {

    /**
     * Validates and creates a new PaymentEmailRequestDTO.
     */
    public PaymentEmailRequestDTO {
        // Generate subject if not provided
        subject = subject != null && !subject.isBlank() ? subject :
                EmailRequestUtils.generatePaymentSubject(template, orderId);

        // Set default timestamp if not provided
        timestamp = EmailRequestUtils.ensureTimestampNotNull(timestamp);

        // Ensure additionalData is not null
        additionalData = EmailRequestUtils.ensureMapNotNull(additionalData);
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
        Map<String, Object> data = EmailRequestUtils.createBaseTemplateData(
                messageId, timestamp, additionalData);

        // Add payment-specific data
        data.put("orderId", orderId);
        data.put("paymentId", paymentId);
        data.put("paymentStatus", paymentStatus);
        data.put("paymentAmount", paymentAmount);

        return data;
    }

    /**
     * Creates a builder with default values.
     */
    public static PaymentEmailRequestDTOBuilder defaultBuilder() {
        return builder()
                .messageId(EmailRequestUtils.generateMessageId())
                .version(EmailRequestUtils.getCurrentVersion())
                .timestamp(LocalDateTime.now());
    }
}
