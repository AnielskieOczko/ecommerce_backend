package com.rj.ecommerce_backend.domain.product.exceptions;

public class InvalidCategoryDataException extends RuntimeException {
    public InvalidCategoryDataException(String message) {
        super(message);
    }
}
