package com.rj.ecommerce_backend.domain.user.dtos;

public record AddressDto(
        String street,
        String city,
        String zipCode,
        String country) {
}
