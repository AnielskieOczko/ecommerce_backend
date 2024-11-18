package com.rj.ecommerce_backend.securityconfig;

import com.rj.ecommerce_backend.securityconfig.dto.JwtResponse;
import com.rj.ecommerce_backend.securityconfig.dto.LoginRequest;
import com.rj.ecommerce_backend.securityconfig.dto.TokenRefreshRequest;

public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);
    JwtResponse refreshToken(TokenRefreshRequest tokenRefreshRequest);
}
