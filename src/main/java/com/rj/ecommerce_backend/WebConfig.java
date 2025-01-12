package com.rj.ecommerce_backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${storage.location}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/v1/public/products/images/**")
                .addResourceLocations("file:" + uploadDir + "/")
                .setCachePeriod(3600); // Optional: add caching
    }


    // added to for class TestDataLoader
    // used for image upload for product initialization (used only to create test products - before server starts)
    @Bean
    public PathMatchingResourcePatternResolver resourcePatternResolver() {
        return new PathMatchingResourcePatternResolver();
    }
}
