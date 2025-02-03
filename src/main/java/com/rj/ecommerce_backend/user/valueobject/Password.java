package com.rj.ecommerce_backend.user.valueobject;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;

@Embeddable
public record Password(@NotEmpty String value) {
    public static Password of(String password) {
        validatePassword(password);
        return new Password(password);
    }

    private static void validatePassword(String password) {
        // Add your password validation logic here
        if (password == null || password.isEmpty() || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
    }
}
