package com.rj.ecommerce_backend.user.valueobject;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;


@Embeddable
public record ZipCode(String value) {
    // validation
}
