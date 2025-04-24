package com.rj.ecommerce_backend.messaging.email.contract.v1.notification;
import com.rj.ecommerce_backend.messaging.email.contract.v1.EmailStatus;
import lombok.Builder;
import lombok.NonNull;
import lombok.With;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object for email delivery status updates.
 */
@Builder
@With
public record EmailDeliveryStatusDTO(
        @NonNull String messageId,
        @NonNull String version,
        String originalMessageId,
        @NonNull EmailStatus status,
        String recipientEmail,
        String errorMessage,
        Map<String, Object> additionalData,
        LocalDateTime timestamp
) {
    /**
     * Validates and creates a new EmailDeliveryStatusDTO with default timestamp if not provided.
     */
    public EmailDeliveryStatusDTO {
        if (messageId.isBlank()) {
            throw new IllegalArgumentException("Message ID cannot be blank");
        }
        if (version.isBlank()) {
            throw new IllegalArgumentException("Version cannot be blank");
        }
        
        // Set default timestamp if not provided
        timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        
        // Ensure additionalData is not null
        additionalData = additionalData != null ? additionalData : Map.of();
    }
    
    /**
     * Creates a success status DTO.
     */
    public static EmailDeliveryStatusDTO success(String originalMessageId, String recipientEmail) {
        return builder()
                .messageId(UUID.randomUUID().toString())
                .version("1.0")
                .originalMessageId(originalMessageId)
                .status(EmailStatus.SENT)
                .recipientEmail(recipientEmail)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Creates a failure status DTO.
     */
    public static EmailDeliveryStatusDTO failure(String originalMessageId, String recipientEmail, String errorMessage) {
        return builder()
                .messageId(UUID.randomUUID().toString())
                .version("1.0")
                .originalMessageId(originalMessageId)
                .status(EmailStatus.FAILED)
                .recipientEmail(recipientEmail)
                .errorMessage(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Creates a builder with default values.
     */
    public static EmailDeliveryStatusDTOBuilder defaultBuilder() {
        return builder()
                .messageId(UUID.randomUUID().toString())
                .version("1.0")
                .timestamp(LocalDateTime.now());
    }
}
