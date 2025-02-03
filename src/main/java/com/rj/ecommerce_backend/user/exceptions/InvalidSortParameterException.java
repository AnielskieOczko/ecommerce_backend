package com.rj.ecommerce_backend.user.exceptions;

public class InvalidSortParameterException extends RuntimeException {
    public InvalidSortParameterException(String message) {
        super(message);
    }
}
