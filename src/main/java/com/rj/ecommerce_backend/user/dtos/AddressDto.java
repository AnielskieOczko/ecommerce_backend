package com.rj.ecommerce_backend.user.dtos;

public record AddressDto(
        String street,
        String city,
        String zipCode,
        String country) {
}
