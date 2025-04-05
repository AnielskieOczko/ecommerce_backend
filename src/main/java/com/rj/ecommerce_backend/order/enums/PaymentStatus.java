package com.rj.ecommerce_backend.order.enums;

public enum PaymentStatus {
    CREATED,            // checkout.session.created
    PROCESSING,         // When payment is being processed
    REQUIRES_ACTION,    // When additional customer action is needed
    SUCCEEDED,          // checkout.session.completed or checkout.session.async_payment_succeeded
    FAILED,             // checkout.session.expired or checkout.session.async_payment_failed
    CANCELED,           // checkout.session.expired
    REFUNDED,           // For future use with refund events
    ERROR;              // For system errors during processing

    public static PaymentStatus fromStripeEvent(String eventName) {
        return switch (eventName) {
            case "checkout.session.created" -> CREATED;
            case "checkout.session.completed", "checkout.session.async_payment_succeeded" -> SUCCEEDED;
            case "checkout.session.expired" -> CANCELED;
            case "checkout.session.async_payment_failed" -> FAILED;
            default -> throw new IllegalArgumentException("Unknown Stripe event: " + eventName);
        };
    }
}