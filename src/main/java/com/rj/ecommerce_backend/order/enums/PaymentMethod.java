package com.rj.ecommerce_backend.order.enums;

public enum PaymentMethod {
    CREDIT_CARD,
    PAYPAL,
    BANK_TRANSFER,
    BLIK;

    public static PaymentMethod fromString(String method) {
        try {
            return PaymentMethod.valueOf(method);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment method: " + method, e);
        }

    }
}
