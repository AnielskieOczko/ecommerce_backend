package com.rj.ecommerce_backend.securityconfig.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TokenInfo {
    private String token;
    private LocalDateTime blacklistedAt;
    private LocalDateTime expiresAt;
    private String blacklistedBy;
}
