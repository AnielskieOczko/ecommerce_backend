package com.rj.ecommerce_backend.messaging.email.contract.v1;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class containing common email request functionality.
 * This is used via composition rather than inheritance to work with records.
 */
public class EmailRequestUtils {

    /**
     * Validates that a string is not blank.
     *
     * @param value The string to validate
     * @param fieldName The name of the field for the error message
     * @return The validated string
     * @throws IllegalArgumentException if the string is blank
     */
    public static String validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value;
    }

    /**
     * Generates a default message ID.
     *
     * @return A new UUID as string
     */
    public static String generateMessageId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Returns the current version.
     *
     * @return The current version string
     */
    public static String getCurrentVersion() {
        return "1.0";
    }

    /**
     * Creates a base template data map with common fields.
     *
     * @param messageId The message ID
     * @param timestamp The timestamp
     * @param additionalData Additional data to include
     * @return A map containing the template data
     */
    public static Map<String, Object> createBaseTemplateData(
            String messageId,
            LocalDateTime timestamp,
            Map<String, Object> additionalData
    ) {
        Map<String, Object> data = new HashMap<>();
        data.put("messageId", messageId);
        data.put("timestamp", timestamp);

        // Add any additional data
        if (additionalData != null && !additionalData.isEmpty()) {
            data.putAll(additionalData);
        }

        return data;
    }

    /**
     * Ensures a map is not null.
     *
     * @param map The map to check
     * @return The original map or an empty map if null
     */
    public static <K, V> Map<K, V> ensureMapNotNull(Map<K, V> map) {
        return map != null ? map : Map.of();
    }

    /**
     * Ensures a timestamp is not null.
     *
     * @param timestamp The timestamp to check
     * @return The original timestamp or the current time if null
     */
    public static LocalDateTime ensureTimestampNotNull(LocalDateTime timestamp) {
        return timestamp != null ? timestamp : LocalDateTime.now();
    }

    /**
     * Generates a subject line for order emails based on the template and order number.
     */
    public static String generateOrderSubject(EmailTemplate template, String orderNumber) {
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

    /**
     * Generates a subject line for payment emails based on the template and order ID.
     */
    public static String generatePaymentSubject(EmailTemplate template, String orderId) {
        return switch (template) {
            case PAYMENT_CONFIRMATION -> "Payment Confirmed - Order #" + orderId;
            case PAYMENT_FAILED -> "Payment Failed - Order #" + orderId;
            case PAYMENT_ERROR_ADMIN -> "Payment Processing Error - Order #" + orderId;
            case PAYMENT_ERROR_CUSTOMER -> "Payment Processing Update - Order #" + orderId;
            default -> "Payment Information - Order #" + orderId;
        };
    }
}
