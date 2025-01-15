package com.rj.ecommerce_backend.domain.product;

import com.rj.ecommerce_backend.domain.product.dtos.ProductResponseDTO;
import com.rj.ecommerce_backend.domain.product.dtos.ProductSearchCriteria;
import com.rj.ecommerce_backend.domain.sortingfiltering.ProductSortField;
import com.rj.ecommerce_backend.domain.sortingfiltering.SortValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Slf4j

@Tag(name = "Product Management", description = "APIs for managing product operations")
public abstract class BaseProductController {

    protected final ProductService productService;
    protected final FileStorageService fileStorageService;
    protected final SortValidator sortValidator;

    @Operation(
            summary = "Get all products with filtering and pagination",
            description = "Retrieves a paginated list of products with optional filtering by search term, category, price range, and stock quantity"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination or sort parameters")
    })
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @Parameter(description = "Search term for product name or description")
            @RequestParam(required = false) String search,
            @Parameter(description = "Category ID to filter products")
            @RequestParam(required = false) String categoryId,
            @Parameter(description = "Minimum price filter")
            @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price filter")
            @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Minimum stock quantity filter")
            @RequestParam(required = false) Integer minStockQuantity,
            @Parameter(description = "Maximum stock quantity filter")
            @RequestParam(required = false) Integer maxStockQuantity,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field and direction (e.g., 'id:asc', 'name:desc')")
            @RequestParam(defaultValue = "id:asc") String sort) {

        log.info("Received request to retrieve products with filters. search={}, categoryId={}, minPrice={}, maxPrice={}, minStockQuantity={}, maxStockQuantity={}",
                search, categoryId, minPrice, maxPrice, minStockQuantity, maxStockQuantity);

        Sort validatedSort = sortValidator.validateAndBuildSort(sort, ProductSortField.class);
        Pageable pageable = PageRequest.of(page, size, validatedSort);
        ProductSearchCriteria criteria = new ProductSearchCriteria(
                search,
                categoryId,
                minPrice,
                maxPrice,
                minStockQuantity,
                maxStockQuantity);

        Page<ProductResponseDTO> products = productService.getAllProducts(pageable, criteria);

        log.info("Successfully retrieve products with filters. search={}, categoryId={}, minPrice={}, maxPrice={}, minStockQuantity={}, maxStockQuantity={}",
                search, categoryId, minPrice, maxPrice, minStockQuantity, maxStockQuantity);

        return ResponseEntity.ok(products);
    }


    @Operation(
            summary = "Get product image",
            description = "Retrieves a product image by its filename"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> getImage(
            @Parameter(description = "Image filename")
            @PathVariable String fileName) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}
