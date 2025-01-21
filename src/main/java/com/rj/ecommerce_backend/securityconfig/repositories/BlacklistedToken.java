package com.rj.ecommerce_backend.securityconfig.repositories;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "blacklisted_tokens")
@NoArgsConstructor
public class BlacklistedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_string", length = 500, nullable = false, unique = true)
    private String tokenString;

    @Column(name = "blacklisted_at", nullable = false)
    private LocalDateTime blacklistedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "blacklisted_by")
    private String blacklistedBy;

    @Column(name = "user_id")
    private Long userId;

    public BlacklistedToken(String token, LocalDateTime expiresAt, String blacklistedBy, Long userId) {
        this.tokenString = token;
        this.blacklistedAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.blacklistedBy = blacklistedBy;
        this.userId = userId;
    }
}
