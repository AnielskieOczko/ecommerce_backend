package com.rj.ecommerce_backend;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI ecommerceOpenApi() {

        return new OpenAPI()
                .info(new Info()
                        .title("E-commerce API Documentation")
                        .description("REST API documentation for E-commerce application")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Rafał Jankowski")
                                .email("rafaljankowski7@gmail.com")));
    }
}
