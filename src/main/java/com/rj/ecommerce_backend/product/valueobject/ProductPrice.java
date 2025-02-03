package com.rj.ecommerce_backend.product.valueobject;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Embeddable
public record ProductPrice(@NotNull @PositiveOrZero Amount amount, @NotNull CurrencyCode currency) {
}
