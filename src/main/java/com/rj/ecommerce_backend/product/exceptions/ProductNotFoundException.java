package com.rj.ecommerce_backend.product.exceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Product not found with ID: " + id);
    }
}
