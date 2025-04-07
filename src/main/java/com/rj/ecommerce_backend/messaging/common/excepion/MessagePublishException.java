package com.rj.ecommerce_backend.messaging.common.excepion;

public class MessagePublishException extends RuntimeException {
    public MessagePublishException(String message, Throwable cause) {
        super(message);
    }
}
