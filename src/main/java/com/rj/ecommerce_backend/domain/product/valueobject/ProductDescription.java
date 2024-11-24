package com.rj.ecommerce_backend.domain.product.valueobject;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Embeddable
public record ProductDescription (@NotBlank @Size(min = 3, max = 255) String value) {
}
