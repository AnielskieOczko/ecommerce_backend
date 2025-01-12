package com.rj.ecommerce_backend.domain.user.exceptions;

public class InvalidSortParameterException extends RuntimeException {
    public InvalidSortParameterException(String message) {
        super(message);
    }
}
