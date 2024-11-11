package com.rj.ecommerce_backend.domain.user.valueobject;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;

@Embeddable
public record Email(@NotEmpty String value) {
    public static Email of(String email) {
        validateEmail(email);
        return new Email(email);
    }

    private static void validateEmail(String email) {
        // Add your email validation logic here
        if (email == null || email.isEmpty() || !isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email address");
        }
    }

    private static boolean isValidEmail(String email) {
        // Add your email validation implementation here
        return true;
    }
}
