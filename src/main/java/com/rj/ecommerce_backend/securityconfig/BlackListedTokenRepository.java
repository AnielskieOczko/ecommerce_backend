package com.rj.ecommerce_backend.securityconfig;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BlackListedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    boolean existsByTokenString(String tokenString);

    @Query("SELECT b FROM BlacklistedToken b WHERE b.expiresAt <= :expiryDate")
    List<BlacklistedToken> findExpiredTokens(@Param("expiryDate") LocalDateTime expiryDate);

    @Modifying
    @Query("DELETE FROM BlacklistedToken b WHERE b.expiresAt <= :expiryDate")
    int deleteExpiredTokens(@Param("expiryDate") LocalDateTime expiryDate);

    List<BlacklistedToken> findByUserId(Long userId);
}
