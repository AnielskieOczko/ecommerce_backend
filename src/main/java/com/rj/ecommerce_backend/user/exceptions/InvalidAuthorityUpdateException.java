package com.rj.ecommerce_backend.user.exceptions;

public class InvalidAuthorityUpdateException extends RuntimeException {
    public InvalidAuthorityUpdateException(String message) {
        super(message);
    }
}
