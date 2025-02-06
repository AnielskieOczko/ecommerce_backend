package com.rj.ecommerce_backend.product.exceptions;

public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(Long imageId) {
        super("Image not found with id: " + imageId);
    }
}
