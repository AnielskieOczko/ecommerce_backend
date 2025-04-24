package com.rj.ecommerce_backend.messaging.email.contract.v1.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rj.ecommerce_backend.messaging.email.contract.v1.EcommerceEmailRequest;
import com.rj.ecommerce_backend.messaging.email.contract.v1.EmailRequestUtils;
import com.rj.ecommerce_backend.messaging.email.contract.v1.EmailTemplate;
import com.rj.ecommerce_backend.messaging.email.contract.v1.common.AddressDTO;
import com.rj.ecommerce_backend.messaging.email.contract.v1.common.CustomerDTO;
import com.rj.ecommerce_backend.messaging.email.contract.v1.common.MoneyDTO;
import com.rj.ecommerce_backend.order.enums.OrderStatus;
import com.rj.ecommerce_backend.order.enums.PaymentMethod;
import com.rj.ecommerce_backend.order.enums.ShippingMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.NonNull;
import lombok.With;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for order-related email requests.
 */
@Builder
@With
public record OrderEmailRequestDTO(
        @NonNull @NotBlank String messageId,
        @NonNull @NotBlank String version,
        @NonNull @NotBlank String to,
        String subject,
        @NonNull EmailTemplate template,
        @NonNull @NotBlank String orderId,
        String orderNumber,
        CustomerDTO customer,
        @NonNull @NotEmpty List<OrderItemDTO> items,
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
        // Validate required fields (additional validation beyond @NonNull and @NotBlank)
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        // Generate subject if not provided
        subject = subject != null && !subject.isBlank() ? subject :
                EmailRequestUtils.generateOrderSubject(template, orderNumber);

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

        // Add order-specific data
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

        return data;
    }

    /**
     * Creates a builder with default values.
     */
    public static OrderEmailRequestDTOBuilder defaultBuilder() {
        return builder()
                .messageId(EmailRequestUtils.generateMessageId())
                .version(EmailRequestUtils.getCurrentVersion())
                .timestamp(LocalDateTime.now());
    }
}
