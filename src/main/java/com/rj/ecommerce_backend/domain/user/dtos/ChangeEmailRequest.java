package com.rj.ecommerce_backend.domain.user.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChangeEmailRequest(
        @NotNull
        String currentPassword,
        @NotNull
        String newEmail
) {}
