package com.rj.ecommerce_backend.domain.product.exceptions;

public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(Long imageId) {
        super("Image not found with id: " + imageId);
    }
}
