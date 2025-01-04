package com.rj.ecommerce_backend.domain.product;

import com.rj.ecommerce_backend.domain.product.exceptions.FileStorageException;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@ConfigurationProperties(prefix = "storage")
@Validated
@Getter
@Setter
@Slf4j
public class StorageProperties {
    @NotNull
    private String location;

    @NotNull
    private String baseUrl = "/images";

    private String cleanupSchedule;

    private Boolean cleanupEnabled = true;

    private Integer cleanupThresholdDays = 7;

    private String tempDir;

    // The initialization method needs to create necessary directories
    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(location);
            Files.createDirectories(uploadPath);

            if (tempDir != null) {
                Path tempPath = Paths.get(tempDir);
                Files.createDirectories(tempPath);
            }

            log.info("Storage initialized at location: {}", location);

        } catch (IOException e) {
            throw new FileStorageException("Could not initialize storage", e);
        }
    }
}
