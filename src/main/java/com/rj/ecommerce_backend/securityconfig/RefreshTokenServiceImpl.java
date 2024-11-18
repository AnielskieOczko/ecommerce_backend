package com.rj.ecommerce_backend.securityconfig;

import com.rj.ecommerce_backend.domain.user.User;
import com.rj.ecommerce_backend.domain.user.UserRepository;
import com.rj.ecommerce_backend.domain.user.UserService;
import com.rj.ecommerce_backend.securityconfig.exception.TokenRefreshException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService{

//    @Value("${spring.config.jwt.refresh-token-expiration}")
    private final Long refreshTokenDurationMs = 86400000L;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final HttpServletRequest request;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        log.debug("Creating new refresh token for user: {}", userId);
        RefreshToken refreshToken = new RefreshToken();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Remove existing refresh token
        refreshTokenRepository.deleteByUserId(user.getId());

        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(generateRefreshToken());
        refreshToken.setCreatedByIp(getClientIp());

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenWithUser(token)
                .orElseThrow(() -> new TokenRefreshException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new TokenRefreshException("Refresh token was expired");
        }

        return refreshToken;
    }

    private String generateRefreshToken() {
        // Using UUID for refresh token
        return UUID.randomUUID().toString();
    }

    private String getClientIp() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}