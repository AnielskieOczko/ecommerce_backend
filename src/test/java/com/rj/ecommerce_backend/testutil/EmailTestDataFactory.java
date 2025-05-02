package com.rj.ecommerce_backend.testutil;

import com.rj.ecommerce_backend.messaging.email.contract.v1.customer.WelcomeEmailRequestDTO;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Factory class for creating test email data
 */
public class EmailTestDataFactory {

    /**
     * Creates a valid WelcomeEmailRequestDTO with default values
     */
    public static WelcomeEmailRequestDTO createValidWelcomeEmailRequest() {
        return WelcomeEmailRequestDTO.builder()
                .messageId(UUID.randomUUID().toString())
                .version("1.0")
                .to("customer@example.com")
                .subject("Welcome to Our Store!")
                .customerName("John Doe")
                .couponCode("WELCOME10")
                .additionalData(Map.of("key1", "value1"))
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a valid WelcomeEmailRequestDTO with custom values
     */
    public static WelcomeEmailRequestDTO createCustomWelcomeEmailRequest(
            String to, 
            String customerName, 
            String couponCode, 
            Map<String, Object> additionalData) {
        
        return WelcomeEmailRequestDTO.builder()
                .messageId(UUID.randomUUID().toString())
                .version("1.0")
                .to(to)
                .subject("Welcome to Our Store!")
                .customerName(customerName)
                .couponCode(couponCode)
                .additionalData(additionalData != null ? additionalData : new HashMap<>())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
