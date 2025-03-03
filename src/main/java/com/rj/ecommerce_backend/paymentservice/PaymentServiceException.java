package com.rj.ecommerce_backend.paymentservice;

public class PaymentServiceException extends RuntimeException {
    public PaymentServiceException(String message, Throwable cause) {
        super(message);
    }
}
