package com.rj.ecommerce_backend.domain.user.dtos;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {}
