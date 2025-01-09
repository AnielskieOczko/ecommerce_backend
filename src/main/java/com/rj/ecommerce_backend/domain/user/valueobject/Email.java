package com.rj.ecommerce_backend.domain.user.valueobject;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;

@Embeddable
public record Email(String value) {
    public static Email of(String email) {
        return new Email(email);
    }

    private static boolean isValidEmail(String email) {
        // Add your email validation implementation here
        return true;
    }
}
