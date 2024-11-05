package com.rj.ecommerce_backend.securityconfig;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private boolean success;
    private String message;
    private JwtResponse data;
}
