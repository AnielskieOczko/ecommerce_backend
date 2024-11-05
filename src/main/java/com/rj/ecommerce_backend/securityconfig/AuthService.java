package com.rj.ecommerce_backend.securityconfig;

public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);
    JwtResponse refreshToken(String refreshToken);
}
