package com.rj.ecommerce_backend.securityconfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
@Component
public class JwtConfig {
    private String secret;
    private int expirationMs;
}
