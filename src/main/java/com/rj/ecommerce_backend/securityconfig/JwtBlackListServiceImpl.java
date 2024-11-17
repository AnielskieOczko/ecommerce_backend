package com.rj.ecommerce_backend.securityconfig;

import com.rj.ecommerce_backend.securityconfig.dto.TokenInfo;
import com.rj.ecommerce_backend.securityconfig.exception.TokenBlacklistException;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class JwtBlackListServiceImpl implements JwtBlacklistService {

    private final BlackListedTokenRepository blackListedTokenRepository;
    private final JwtUtils jwtUtils;

    @Override
    public void blacklistToken(String token, String username) {
        try {
            Claims claims = jwtUtils.getTokenClaims(token);
            Long userId = Long.valueOf(claims.getSubject());
            Date expirationDate = claims.getExpiration();

            BlacklistedToken blacklistedToken = new BlacklistedToken(
                    token,
                    LocalDateTime.ofInstant(expirationDate.toInstant(), ZoneId.systemDefault()),
                    username,
                    userId
            );

            blackListedTokenRepository.save(blacklistedToken);
            log.info("Token blacklisted successfully for user: {}", username);

        } catch (Exception e) {
            log.error("Error blacklisting token", e);
            throw new TokenBlacklistException("Failed to blacklist token", e);
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        try {
            return blackListedTokenRepository.existsByTokenString(token);
        } catch (Exception e) {
            log.error("Error checking blacklisted token", e);
            // For security, consider token blacklisted if there's a database error
            return true;
        }
    }

    @Scheduled(cron = "${token.cleanup.cron:0 0 * * * *}") // Default: Run every hour
    public void cleanupExpiredTokens() {
        try {
            LocalDateTime now = LocalDateTime.now();
            int deletedCount = blackListedTokenRepository.deleteExpiredTokens(now);
            log.info("Cleaned up {} expired tokens", deletedCount);
        } catch (Exception e) {
            log.error("Error during token cleanup", e);
        }
    }

    @Override
    public List<TokenInfo> getUserTokens(Long userId) {
        return blackListedTokenRepository.findByUserId(userId).stream()
                .map(token -> new TokenInfo(
                        token.getTokenString(),
                        token.getBlacklistedAt(),
                        token.getExpiresAt(),
                        token.getBlacklistedBy()
                ))
                .collect(Collectors.toList());
    }
}
