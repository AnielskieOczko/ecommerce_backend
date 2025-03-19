package com.rj.ecommerce_backend.messaging.common.excepion;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class PaymentIntentException extends RuntimeException {
    public PaymentIntentException(String message, Throwable cause) {
        super(message);
    }
}
