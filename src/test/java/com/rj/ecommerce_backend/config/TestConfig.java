package com.rj.ecommerce_backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test configuration for integration tests
 */
@TestConfiguration
@ActiveProfiles("test")
public class TestConfig {
    
    // Add test-specific beans here
    
}
