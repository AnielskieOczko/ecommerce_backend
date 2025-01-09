package com.rj.ecommerce_backend.domain.user.dtos;

import jakarta.validation.constraints.NotNull;

public record AccountStatusRequest(
        @NotNull(message = "Account status must be specified")
        Boolean active) {
}
