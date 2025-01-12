package com.rj.ecommerce_backend.domain.product;

import com.rj.ecommerce_backend.domain.product.dtos.ProductResponseDTO;
import com.rj.ecommerce_backend.domain.product.dtos.ProductSearchCriteria;
import com.rj.ecommerce_backend.domain.sortingfiltering.ProductSortField;
import com.rj.ecommerce_backend.domain.sortingfiltering.SortValidator;
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
public abstract class BaseProductController {

    protected final ProductService productService;
    protected final FileStorageService fileStorageService;
    protected final SortValidator sortValidator;

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minStockQuantity,
            @RequestParam(required = false) Integer maxStockQuantity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
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

    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}
