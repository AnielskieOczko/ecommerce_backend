package com.rj.ecommerce_backend.messaging.email.contract.v1.order;

import com.rj.ecommerce_backend.messaging.email.contract.v1.common.MoneyDTO;
import lombok.Builder;
import lombok.NonNull;
import lombok.With;

/**
 * Data Transfer Object for order item information.
 */
@Builder
@With
public record OrderItemDTO(
        String id,
        @NonNull String productId,
        @NonNull String productName,
        String productSku,
        int quantity,
        @NonNull MoneyDTO unitPrice,
        MoneyDTO totalPrice
) {
    /**
     * Validates and creates a new OrderItemDTO.
     */
    public OrderItemDTO {
        if (productId.isBlank()) {
            throw new IllegalArgumentException("Product ID cannot be blank");
        }
        if (productName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        // Normalize ID (can be null for new items)
        id = id == null || id.isBlank() ? null : id;

        // Normalize SKU (optional)
        productSku = productSku == null || productSku.isBlank() ? null : productSku;
    }
}
