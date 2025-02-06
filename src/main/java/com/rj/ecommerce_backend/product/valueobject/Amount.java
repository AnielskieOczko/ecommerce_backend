package com.rj.ecommerce_backend.product.valueobject;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public record Amount(@NotNull @PositiveOrZero BigDecimal value) {

    public Amount {
        Objects.requireNonNull(value, "Amount value cannot be null");
        if(value.scale() > 2 ) {
            throw new IllegalArgumentException("incorrect number of decimal places");
        }
    }
}
