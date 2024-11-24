package com.rj.ecommerce_backend.domain.product.exceptions;

public class CategoryAlreadyExistsException extends RuntimeException {
    public CategoryAlreadyExistsException(String name) {
        super("Category with name '" + name + "' already exists.");
    }
    public CategoryAlreadyExistsException(Long id) {
        super("Category with id '" + id + "' already exists.");
    }
}
