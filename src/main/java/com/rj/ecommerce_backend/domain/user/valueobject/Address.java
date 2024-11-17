package com.rj.ecommerce_backend.domain.user.valueobject;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;

@Embeddable
public record Address(
        @NotEmpty String street,
        @NotEmpty String city,
        ZipCode zipCode,
        @NotEmpty String country
) { }
