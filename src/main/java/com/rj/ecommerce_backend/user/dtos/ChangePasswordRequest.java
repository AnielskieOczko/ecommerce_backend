package com.rj.ecommerce_backend.user.dtos;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {}
