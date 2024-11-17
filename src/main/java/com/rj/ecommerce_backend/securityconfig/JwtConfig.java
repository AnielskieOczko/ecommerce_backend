package com.rj.ecommerce_backend.securityconfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
@Component
@EnableScheduling
public class JwtConfig {
    private String secret;
    private int expirationMs;

    private Cleanup cleanup = new Cleanup();

    @Getter
    @Setter
    public static class Cleanup {
        private String cron = "0 0 * * * *"; // Default: every hour
        private int batchSize = 1000;
    }
}
