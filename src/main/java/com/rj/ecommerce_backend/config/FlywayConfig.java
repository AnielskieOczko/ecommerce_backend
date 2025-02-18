package com.rj.ecommerce_backend.config;

import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Bean
    public FlywayConfigurationCustomizer flywayConfigurationCustomizer() {
        return configuration -> configuration
                .baselineOnMigrate(true)
                .baselineDescription("Initial Baseline")
                .baselineVersion("0")
                .defaultSchema("ecommerce_dev");
    }
}
