package com.rj.ecommerce_backend.securityconfig;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(Long userId);
    RefreshToken verifyRefreshToken(String token);
}
