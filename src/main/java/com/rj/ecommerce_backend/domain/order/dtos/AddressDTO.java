package com.rj.ecommerce_backend.domain.order.dtos;

public record AddressDTO(
        String street,
        String city,
        String zipCode,
        String country) {
}
