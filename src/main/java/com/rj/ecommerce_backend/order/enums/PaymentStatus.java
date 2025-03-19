package com.rj.ecommerce_backend.order.enums;

public enum PaymentStatus {
    CREATED,            // payment_intent.created
    PROCESSING,         // When payment is being processed
    REQUIRES_ACTION,    // When additional action is needed (3D Secure, etc.)
    SUCCEEDED,          // payment_intent.succeeded or charge.succeeded
    FAILED,            // payment_intent.failed or charge.failed
    CANCELED,          // payment_intent.canceled
    REFUNDED,          // For future use with refund events
    ERROR;             // For system errors during processing

    public static PaymentStatus fromStripeEvent(String eventName) {
        return switch (eventName) {
            case "payment_intent.created" -> CREATED;
            case "payment_intent.succeeded", "charge.succeeded" -> SUCCEEDED;
            case "payment_intent.failed", "charge.failed" -> FAILED;
            case "payment_intent.canceled" -> CANCELED;
            default -> throw new IllegalArgumentException("Unknown Stripe event: " + eventName);
        };
    }
}
