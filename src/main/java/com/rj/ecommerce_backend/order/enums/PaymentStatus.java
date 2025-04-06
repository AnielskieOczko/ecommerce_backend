package com.rj.ecommerce_backend.order.enums;


import lombok.Getter;

/**
 * Enum representing Stripe payment statuses.
 * Based on Stripe's official payment and checkout session statuses.
 * @see <a href="https://stripe.com/docs/api/charges/object#charge_object-status">Stripe Charge Status</a>
 * @see <a href="https://stripe.com/docs/api/checkout/sessions/object#checkout_session_object-payment_status">Stripe Checkout Session Payment Status</a>
 * @see <a href="https://stripe.com/docs/api/checkout/sessions/object#checkout_session_object-status">Stripe Checkout Session Status</a>
 */
@Getter
public enum PaymentStatus {
    // Charge statuses
    SUCCEEDED("succeeded"),
    PENDING("pending"),
    FAILED("failed"),

    // Checkout Session payment_status
    PAID("paid"),
    UNPAID("unpaid"),
    NO_PAYMENT_REQUIRED("no_payment_required"),

    // Checkout Session status
    OPEN("open"),
    COMPLETE("complete"),
    EXPIRED("expired"),

    // Special case
    UNKNOWN("unknown");

    private final String stripeStatus;

    PaymentStatus(String stripeStatus) {
        this.stripeStatus = stripeStatus;
    }

    /**
     * Converts a Stripe charge status to the corresponding PaymentStatus enum.
     */
    public static PaymentStatus fromChargeStatus(String stripeStatus) {
        if (stripeStatus == null || stripeStatus.isEmpty()) {
            return UNKNOWN;
        }

        return switch (stripeStatus.toLowerCase()) {
            case "succeeded" -> SUCCEEDED;
            case "pending" -> PENDING;
            case "failed" -> FAILED;
            default -> UNKNOWN;
        };
    }

    /**
     * Converts a Stripe checkout session payment status to the corresponding PaymentStatus enum.
     */
    public static PaymentStatus fromCheckoutSessionPaymentStatus(String stripeStatus) {
        if (stripeStatus == null || stripeStatus.isEmpty()) {
            return UNKNOWN;
        }

        return switch (stripeStatus.toLowerCase()) {
            case "paid" -> PAID;
            case "unpaid" -> UNPAID;
            case "no_payment_required" -> NO_PAYMENT_REQUIRED;
            default -> UNKNOWN;
        };
    }

    /**
     * Converts a Stripe checkout session status to the corresponding PaymentStatus enum.
     */
    public static PaymentStatus fromCheckoutSessionStatus(String stripeStatus) {
        if (stripeStatus == null || stripeStatus.isEmpty()) {
            return UNKNOWN;
        }

        return switch (stripeStatus.toLowerCase()) {
            case "open" -> OPEN;
            case "complete" -> COMPLETE;
            case "expired" -> EXPIRED;
            default -> UNKNOWN;
        };
    }
}