package com.rj.ecommerce_backend.securityconfig.services;

import com.rj.ecommerce_backend.securityconfig.dto.TokenInfo;

import java.util.List;

public interface JwtBlacklistService {
    void blacklistToken(String token, String username);
    boolean isTokenBlacklisted(String token);
    List<TokenInfo> getUserTokens(Long userId);
}
