package com.rj.ecommerce_backend.product.controller;

import com.rj.ecommerce_backend.product.service.FileStorageService;
import com.rj.ecommerce_backend.product.service.ProductService;
import com.rj.ecommerce_backend.product.dtos.ProductCreateDTO;
import com.rj.ecommerce_backend.product.dtos.ProductResponseDTO;
import com.rj.ecommerce_backend.product.dtos.ProductUpdateDTO;
import com.rj.ecommerce_backend.product.exceptions.ProductNotFoundException;
import com.rj.ecommerce_backend.sorting.SortValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;


@Tag(name = "Admin Product Management", description = "Administrative APIs for product management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/admin/products")
@Slf4j
public class AdminProductController extends BaseProductController {

    public AdminProductController(ProductService productService,
                                  FileStorageService fileStorageService,
                                  SortValidator sortValidator) {
        super(productService, fileStorageService, sortValidator);
    }

    @Operation(
            summary = "Get product by ID",
            description = "Retrieves a specific product by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @Parameter(description = "ID of the product to retrieve")
            @PathVariable Long productId) {
        return productService.getProductById(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProduct(productId);
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

    @Operation(
            summary = "Create new product",
            description = "Creates a new product with optional images"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product data")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Parameter(description = "Product data")
            @RequestPart("product") @Valid ProductCreateDTO productDTO,
            @Parameter(description = "Product images (optional)")
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

    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<Void> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        productService.deleteProductImage(productId, imageId);
        return ResponseEntity.noContent().build();
    }
}
