package com.rj.ecommerce_backend.securityconfig.services;

import com.rj.ecommerce_backend.securityconfig.RefreshToken;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(Long userId);
    RefreshToken verifyRefreshToken(String token);
}
