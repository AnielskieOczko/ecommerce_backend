package com.rj.ecommerce_backend.securityconfig.services;

import com.rj.ecommerce_backend.domain.user.User;
import com.rj.ecommerce_backend.securityconfig.dto.AuthResponse;
import com.rj.ecommerce_backend.securityconfig.dto.LoginRequest;
import com.rj.ecommerce_backend.securityconfig.dto.TokenRefreshRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    AuthResponse authenticateUser(LoginRequest loginRequest);
    AuthResponse refreshToken(TokenRefreshRequest tokenRefreshRequest);
    AuthResponse handleEmailUpdate(
            User user,
            String currentPassword,
            HttpServletRequest request,
            HttpServletResponse response);
}
