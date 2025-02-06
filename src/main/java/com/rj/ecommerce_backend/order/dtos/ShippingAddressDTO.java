package com.rj.ecommerce_backend.order.dtos;

import jakarta.validation.constraints.NotBlank;

public record ShippingAddressDTO(
        @NotBlank String street,
        @NotBlank String city,
        @NotBlank String zipCode,
        @NotBlank String country) {
}
