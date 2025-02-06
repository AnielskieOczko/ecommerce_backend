package com.rj.ecommerce_backend.user.valueobject;

import jakarta.persistence.Embeddable;

@Embeddable
public record PhoneNumber(String value) {
}