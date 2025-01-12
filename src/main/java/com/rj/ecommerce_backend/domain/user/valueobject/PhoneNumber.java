package com.rj.ecommerce_backend.domain.user.valueobject;

import jakarta.persistence.Embeddable;

@Embeddable
public record PhoneNumber(String value) {
//    private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    // TODO: check if there is any lib for phone validation
}