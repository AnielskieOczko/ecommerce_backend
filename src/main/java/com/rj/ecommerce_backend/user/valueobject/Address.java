package com.rj.ecommerce_backend.user.valueobject;

import jakarta.persistence.Embeddable;
import lombok.Builder;

@Builder
@Embeddable
public record Address(
        String street,
        String city,
        ZipCode zipCode,
        String country
) { }
