package com.rj.ecommerce_backend.domain.user.valueobject;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;

@Embeddable
public record Address(
        String street,
        String city,
        ZipCode zipCode,
        String country
) { }
