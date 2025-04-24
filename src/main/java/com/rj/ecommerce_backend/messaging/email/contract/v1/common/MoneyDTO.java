package com.rj.ecommerce_backend.messaging.email.contract.v1.common;

import lombok.Builder;
import lombok.NonNull;
import lombok.With;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Data Transfer Object for monetary values.
 */
@Builder
@With
public record MoneyDTO(
        @NonNull BigDecimal amount,
        @NonNull String currencyCode
) {
    /**
     * Validates and creates a new MoneyDTO.
     */
    public MoneyDTO {
        if (currencyCode.isBlank()) {
            throw new IllegalArgumentException("Currency code cannot be blank");
        }

        // Ensure amount has exactly 2 decimal places
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Creates a MoneyDTO with the specified amount and currency.
     */
    public static MoneyDTO of(BigDecimal amount, String currencyCode) {
        return new MoneyDTO(amount, currencyCode);
    }

    /**
     * Creates a MoneyDTO with the specified amount and currency.
     */
    public static MoneyDTO of(double amount, String currencyCode) {
        return new MoneyDTO(BigDecimal.valueOf(amount), currencyCode);
    }

    /**
     * Creates a MoneyDTO with the specified amount and currency.
     */
    public static MoneyDTO of(String amount, String currencyCode) {
        return new MoneyDTO(new BigDecimal(amount), currencyCode);
    }

    /**
     * Returns a formatted string representation of the monetary value.
     */
    public String formatted() {
        return amount.toString() + " " + currencyCode;
    }
}
