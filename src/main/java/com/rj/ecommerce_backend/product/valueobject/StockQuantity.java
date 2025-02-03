package com.rj.ecommerce_backend.product.valueobject;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public record StockQuantity(@NotBlank int value) {
}
