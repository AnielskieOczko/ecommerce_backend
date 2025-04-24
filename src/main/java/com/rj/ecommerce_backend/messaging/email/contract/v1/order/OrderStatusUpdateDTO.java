package com.rj.ecommerce_backend.messaging.email.contract.v1.order;

import com.rj.ecommerce_backend.order.enums.OrderStatus;
import lombok.Builder;
import lombok.NonNull;
import lombok.With;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object for order status updates.
 */
@Builder
@With
public record OrderStatusUpdateDTO(
        @NonNull String messageId,
        @NonNull String version,
        @NonNull String orderId,
        String orderNumber,
        OrderStatus previousStatus,
        @NonNull OrderStatus newStatus,
        String reason,
        Map<String, Object> additionalData,
        LocalDateTime timestamp
) {
    /**
     * Validates and creates a new OrderStatusUpdateDTO.
     */
    public OrderStatusUpdateDTO {
        if (messageId.isBlank()) {
            throw new IllegalArgumentException("Message ID cannot be blank");
        }
        if (version.isBlank()) {
            throw new IllegalArgumentException("Version cannot be blank");
        }
        if (orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID cannot be blank");
        }
        
        // Set default timestamp if not provided
        timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        
        // Ensure additionalData is not null
        additionalData = additionalData != null ? additionalData : Map.of();
    }
    
    /**
     * Creates a builder with default values.
     */
    public static OrderStatusUpdateDTOBuilder defaultBuilder() {
        return builder()
                .messageId(UUID.randomUUID().toString())
                .version("1.0")
                .timestamp(LocalDateTime.now());
    }
}
