package com.rj.ecommerce_backend.messaging.email.contract.v1.common;

import lombok.Builder;
import lombok.NonNull;
import lombok.With;


/**
 * Data Transfer Object for address information.
 */
@Builder
@With
public record AddressDTO(
        @NonNull String street,
        @NonNull String city,
        @NonNull String zipCode,
        @NonNull String country
) {
    /**
     * Validates and creates a new AddressDTO.
     */
    public AddressDTO {
        if (street.isBlank()) {
            throw new IllegalArgumentException("Street cannot be blank");
        }
        if (city.isBlank()) {
            throw new IllegalArgumentException("City cannot be blank");
        }
        if (zipCode.isBlank()) {
            throw new IllegalArgumentException("Zip code cannot be blank");
        }
        if (country.isBlank()) {
            throw new IllegalArgumentException("Country cannot be blank");
        }
    }
}

