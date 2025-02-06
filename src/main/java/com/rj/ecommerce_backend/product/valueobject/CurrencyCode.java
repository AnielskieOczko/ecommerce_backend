package com.rj.ecommerce_backend.product.valueobject;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.util.Currency;
import java.util.Objects;

@Embeddable
public record CurrencyCode(@NotNull String code) {

    public CurrencyCode {
        Objects.requireNonNull(code, "Currency code cannot be null");
        try {
            Currency.getInstance(code);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid currency code: " + code, e);
        }
    }
}
