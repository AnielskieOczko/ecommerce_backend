package com.rj.ecommerce_backend.user.dtos;

import jakarta.validation.constraints.NotNull;

public record ChangeEmailRequest(
        @NotNull
        String currentPassword,
        @NotNull
        String newEmail
) {}
