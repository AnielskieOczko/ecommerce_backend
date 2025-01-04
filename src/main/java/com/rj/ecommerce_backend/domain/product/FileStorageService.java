package com.rj.ecommerce_backend.domain.product;

import com.rj.ecommerce_backend.domain.product.exceptions.FileStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {
    private final StorageProperties storageProperties;
    private final ImageRepository imageRepository;

    public Image storeFile(MultipartFile file, String altText, Product product) {
        log.debug("Storing file: {} with alt text: {}", file.getOriginalFilename(), altText);

        try {
            String fileName = generateUniqueFileName(file);
            Path targetLocation = getTargetLocation(fileName);

            // Store file with proper error handling
            storeFileWithRetry(file, targetLocation);

            // Create relative URL path - only store the filename in the database
            String urlPath = fileName;

            // Create and save image entity
            Image image = buildImageEntity(urlPath, altText, file.getContentType());
            image.setProduct(product);

            Image savedImage = imageRepository.save(image);

            log.info("Successfully stored file: {} with ID: {} and associated with product ID: {}",
                    fileName, savedImage.getId(), product.getId());
            return savedImage;

        } catch (IOException ex) {
            log.error("Failed to store file: {}", file.getOriginalFilename(), ex);
            throw new FileStorageException("Could not store file " + file.getOriginalFilename(), ex);
        }
    }

    private String generateUniqueFileName(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = StringUtils.getFilenameExtension(originalFileName);
        return UUID.randomUUID() + (fileExtension != null ? "." + fileExtension : "");
    }

    private Path getTargetLocation(String fileName) {
        return Paths.get(storageProperties.getLocation()).resolve(fileName).normalize();
    }

    private void storeFileWithRetry(MultipartFile file, Path targetLocation) throws IOException {
        int maxRetries = 3;
        int attempts = 0;

        while (attempts < maxRetries) {
            try {
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                return;
            } catch (IOException e) {
                attempts++;
                if (attempts == maxRetries) {
                    throw e;
                }
                log.warn("Retry {} of {} for storing file {}",
                        attempts, maxRetries, file.getOriginalFilename());
                try {
                    Thread.sleep(1000); // Wait before retry
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new FileStorageException("Storage interrupted", ie);
                }
            }
        }
    }

    private Image buildImageEntity(String path, String altText, String mimeType) {
        return Image.builder()
                .path(path)
                .altText(altText != null ? altText : "Product image")
                .mimeType(mimeType)
//                .createdAt(LocalDateTime.now())
                .build();
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = Paths.get(storageProperties.getLocation())
                    .resolve(fileName)
                    .normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if(resource.exists()) {
                return resource;
            } else {
                log.warn("File not found: {}", fileName);
                throw new FileNotFoundException("File not found: " + fileName);
            }
        } catch (MalformedURLException | FileNotFoundException ex) {
            throw new FileStorageException("File not found: " + fileName, ex);
        }
    }

    public void deleteImage(Image image) {
        log.debug("Deleting image with ID: {} and path: {}", image.getId(), image.getPath());

        try {
            // The path stored in the database is now just the filename
            String fileName = image.getPath();

            // Get the full path to the file
            Path filePath = Paths.get(storageProperties.getLocation())
                    .resolve(fileName)
                    .normalize();

            // Delete the physical file
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Successfully deleted file: {}", fileName);
            } else {
                log.warn("File not found for deletion: {}", fileName);
            }

            // Delete the image entity from database
            imageRepository.delete(image);
            log.info("Successfully deleted image entity with ID: {}", image.getId());

        } catch (IOException ex) {
            log.error("Error deleting image file for image ID: {}", image.getId(), ex);
            throw new FileStorageException("Could not delete file for image: " + image.getId(), ex);
        }
    }
}
