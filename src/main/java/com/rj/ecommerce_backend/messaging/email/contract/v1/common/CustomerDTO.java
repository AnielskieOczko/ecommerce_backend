package com.rj.ecommerce_backend.messaging.email.contract.v1.common;


import lombok.Builder;
import lombok.NonNull;
import lombok.With;

/**
 * Data Transfer Object for customer information.
 */
@Builder
@With
public record CustomerDTO(
        @NonNull String id,
        String firstName,
        String lastName,
        @NonNull String email,
        String phoneNumber
) {
    /**
     * Validates and creates a new CustomerDTO.
     */
    public CustomerDTO {
        if (id.isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be blank");
        }
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }

        // Normalize empty strings to null for optional fields
        firstName = firstName == null || firstName.isBlank() ? null : firstName;
        lastName = lastName == null || lastName.isBlank() ? null : lastName;
        phoneNumber = phoneNumber == null || phoneNumber.isBlank() ? null : phoneNumber;
    }

    /**
     * Simple email validation.
     */
    private static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Returns the full name of the customer.
     */
    public String fullName() {
        if (firstName == null && lastName == null) {
            return null;
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }
}
