package com.rj.ecommerce_backend.product.exceptions;

public class InvalidCategoryDataException extends RuntimeException {
    public InvalidCategoryDataException(String message) {
        super(message);
    }
}
