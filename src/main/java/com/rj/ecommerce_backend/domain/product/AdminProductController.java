package com.rj.ecommerce_backend.domain.product;

import com.rj.ecommerce_backend.domain.product.dtos.ImageDTO;
import com.rj.ecommerce_backend.domain.product.dtos.ProductCreateDTO;
import com.rj.ecommerce_backend.domain.product.dtos.ProductResponseDTO;
import com.rj.ecommerce_backend.domain.product.dtos.ProductUpdateDTO;
import com.rj.ecommerce_backend.domain.product.exceptions.ProductNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@Slf4j
public class AdminProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<ProductResponseDTO> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }


    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponseDTO>> getProductByCategoryId(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        Page<ProductResponseDTO> productPage = productService.findProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(productPage);


    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (ProductNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDTO>> searchProducts(
            @RequestParam(name = "name", required = false) String productName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<ProductResponseDTO> searchResults = productService.searchProductsByName(productName, pageable);
        return ResponseEntity.ok(searchResults);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProductResponseDTO> createProduct(
            @RequestPart("product") @Valid ProductCreateDTO productDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        ProductResponseDTO createdProduct = productService.createProduct(productDTO, images);

        return ResponseEntity.created(URI.create("/api/products/" + createdProduct.id()))
                .body(createdProduct);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") @Valid ProductUpdateDTO productDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        ProductResponseDTO updatedProduct = productService.updateProduct(id, productDTO, images);
        if (updatedProduct == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedProduct);
    }

    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // You might want to make this dynamic
                .body(resource);
    }

    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<Void> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        productService.deleteProductImage(productId, imageId);
        return ResponseEntity.noContent().build();
    }
}
