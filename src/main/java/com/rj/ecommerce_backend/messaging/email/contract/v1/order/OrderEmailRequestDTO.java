package com.rj.ecommerce_backend.messaging.email.contract.v1.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rj.ecommerce_backend.messaging.email.contract.v1.EcommerceEmailRequest;
import com.rj.ecommerce_backend.messaging.email.contract.v1.EmailTemplate;
import com.rj.ecommerce_backend.messaging.email.contract.v1.common.AddressDTO;
import com.rj.ecommerce_backend.messaging.email.contract.v1.common.CustomerDTO;
import com.rj.ecommerce_backend.messaging.email.contract.v1.common.MoneyDTO;
import com.rj.ecommerce_backend.order.enums.OrderStatus;
import com.rj.ecommerce_backend.order.enums.PaymentMethod;
import com.rj.ecommerce_backend.order.enums.ShippingMethod;
import lombok.Builder;
import lombok.NonNull;
import lombok.With;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object for order-related email requests.
 */
@Builder
@With
public record OrderEmailRequestDTO(
        @NonNull String messageId,
        @NonNull String version,
        @NonNull String to,
        String subject,
        @NonNull EmailTemplate template,
        @NonNull String orderId,
        String orderNumber,
        CustomerDTO customer,
        @NonNull List<OrderItemDTO> items,
        @NonNull MoneyDTO totalAmount,
        AddressDTO shippingAddress,
        ShippingMethod shippingMethod,
        PaymentMethod paymentMethod,
        String paymentTransactionId,
        @NonNull LocalDateTime orderDate,
        @NonNull OrderStatus orderStatus,
        Map<String, Object> additionalData,
        LocalDateTime timestamp
) implements EcommerceEmailRequest {

    /**
     * Validates and creates a new OrderEmailRequestDTO.
     */
    public OrderEmailRequestDTO {
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
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        // Generate subject if not provided
        subject = subject != null && !subject.isBlank() ? subject : generateSubject(template, orderNumber);

        // Set default timestamp if not provided
        timestamp = timestamp != null ? timestamp : LocalDateTime.now();

        // Ensure additionalData is not null
        additionalData = additionalData != null ? additionalData : Map.of();
    }

    /**
     * Generates a subject line based on the template and order number.
     */
    private static String generateSubject(EmailTemplate template, String orderNumber) {
        String orderRef = orderNumber != null && !orderNumber.isBlank() ?
                " #" + orderNumber : "";

        return switch (template) {
            case ORDER_CONFIRMATION -> "Your Order" + orderRef + " Confirmation";
            case ORDER_SHIPMENT -> "Your Order" + orderRef + " Has Been Shipped";
            case ORDER_CANCELLED -> "Your Order" + orderRef + " Has Been Cancelled";
            case ORDER_REFUNDED -> "Your Order" + orderRef + " Has Been Refunded";
            default -> "Information About Your Order" + orderRef;
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
        data.put("orderNumber", orderNumber);
        data.put("customer", customer);
        data.put("items", items);
        data.put("totalAmount", totalAmount);
        data.put("shippingAddress", shippingAddress);
        data.put("shippingMethod", shippingMethod);
        data.put("paymentMethod", paymentMethod);
        data.put("paymentTransactionId", paymentTransactionId);
        data.put("orderDate", orderDate);
        data.put("orderStatus", orderStatus);

        // Add any additional data
        if (additionalData != null) {
            data.putAll(additionalData);
        }

        return data;
    }

    /**
     * Creates a builder with default values.
     */
    public static OrderEmailRequestDTOBuilder defaultBuilder() {
        return builder()
                .messageId(UUID.randomUUID().toString())
                .version("1.0")
                .timestamp(LocalDateTime.now());
    }
}
