package com.rj.ecommerce_backend.domain.user.valueobject;

import jakarta.persistence.Embeddable;

@Embeddable
public record PhoneNumber(String value) {
    // TODO: check if there is any lib for phone validation
}