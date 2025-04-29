package com.rj.ecommerce_backend.order.enums;

public enum OrderStatus {
    PENDING,        // Order created but not confirmed
    CONFIRMED,      // Order confirmed (payment successful)
    PROCESSING,     // Order is being prepared for shipment
    SHIPPED,        // Order has been shipped
    DELIVERED,      // Order has been delivered
    CANCELLED,      // Order has been cancelled
    REFUNDED,       // Order has been refunded
    FAILED;          // Order failed (e.g., payment failed)

    public static OrderStatus fromString(String orderStatus) {
        try {
            return OrderStatus.valueOf(orderStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + orderStatus, e);
        }
    }

}
