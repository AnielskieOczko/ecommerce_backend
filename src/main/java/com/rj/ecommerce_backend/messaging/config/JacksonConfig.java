package com.rj.ecommerce_backend.messaging.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    /**
     * ObjectMapper specifically for RabbitMQ serialization/deserialization.
     * Includes NON_FINAL default typing.
     */
    @Bean
    @Qualifier("rabbitObjectMapper") // <<< Add Qualifier
    public ObjectMapper rabbitObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Enable default typing ONLY for this mapper
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        // Optional: Configure based on needs for RabbitMQ messages
        // objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return objectMapper;
    }

    /**
     * Primary ObjectMapper for general application use, including Spring MVC.
     * Does NOT have global default typing enabled.
     */
    @Bean
    @Primary // <<< Mark as Primary so Spring MVC picks it up by default
    public ObjectMapper primaryObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // DO NOT enable default typing here globally
        // objectMapper.activateDefaultTyping(...) // <<< REMOVE/COMMENT THIS OUT

        // Configure common settings needed for web/general use
        // Often useful to ignore unknown properties in incoming web requests
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return objectMapper;
    }

}
