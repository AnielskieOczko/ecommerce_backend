package com.rj.ecommerce_backend.messaging.email.contract.v1.customer;

import com.rj.ecommerce_backend.messaging.email.contract.v1.EmailRequestUtils;
import com.rj.ecommerce_backend.messaging.email.contract.v1.EcommerceEmailRequest;
import com.rj.ecommerce_backend.messaging.email.contract.v1.EmailTemplate;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.With;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Data Transfer Object for welcome emails to new customers
 */
@Builder
@With
public record WelcomeEmailRequestDTO(
        @NonNull @NotBlank String messageId,
        @NonNull @NotBlank String version,
        @NonNull @NotBlank String to,
        String subject,
        @NonNull @NotBlank String customerName,
        String couponCode,
        Map<String, Object> additionalData,
        LocalDateTime timestamp
) implements EcommerceEmailRequest {

    /**
     * Validates and creates a new WelcomeEmailRequestDTO with default values.
     */
    public WelcomeEmailRequestDTO {
        // Validate required fields
        messageId = EmailRequestUtils.validateNotBlank(messageId, "Message ID");
        version = EmailRequestUtils.validateNotBlank(version, "Version");
        to = EmailRequestUtils.validateNotBlank(to, "Recipient email");
        customerName = EmailRequestUtils.validateNotBlank(customerName, "Customer name");

        // Set default values
        subject = subject != null && !subject.isBlank() ? subject : "Welcome to Our Store!";
        additionalData = EmailRequestUtils.ensureMapNotNull(additionalData);
        timestamp = EmailRequestUtils.ensureTimestampNotNull(timestamp);
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
        Map<String, Object> data = EmailRequestUtils.createBaseTemplateData(
                messageId, timestamp, additionalData);

        data.put("customerName", customerName);
        if (couponCode != null && !couponCode.isBlank()) {
            data.put("couponCode", couponCode);
        }

        return data;
    }

    /**
     * Creates a builder with default values.
     */
    public static WelcomeEmailRequestDTOBuilder defaultBuilder() {
        return builder()
                .messageId(EmailRequestUtils.generateMessageId())
                .version(EmailRequestUtils.getCurrentVersion())
                .timestamp(LocalDateTime.now());
    }
}