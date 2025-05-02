package com.rj.ecommerce_backend.testutil;

import com.rj.ecommerce_backend.product.domain.Category;
import com.rj.ecommerce_backend.product.domain.Image;
import com.rj.ecommerce_backend.product.domain.Product;
import com.rj.ecommerce_backend.product.dtos.CategoryResponseDTO;
import com.rj.ecommerce_backend.product.dtos.ImageDTO;
import com.rj.ecommerce_backend.product.dtos.ProductCreateDTO;
import com.rj.ecommerce_backend.product.dtos.ProductResponseDTO;
import com.rj.ecommerce_backend.product.dtos.ProductSearchCriteria;
import com.rj.ecommerce_backend.product.dtos.ProductUpdateDTO;
import com.rj.ecommerce_backend.product.valueobject.Amount;
import com.rj.ecommerce_backend.product.valueobject.CurrencyCode;
import com.rj.ecommerce_backend.product.valueobject.ProductDescription;
import com.rj.ecommerce_backend.product.valueobject.ProductName;
import com.rj.ecommerce_backend.product.valueobject.ProductPrice;
import com.rj.ecommerce_backend.product.valueobject.StockQuantity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Factory class for creating test product data
 */
public class ProductTestDataFactory {

    /**
     * Creates a valid Product entity with default values
     */
    public static Product createValidProduct() {
        return Product.builder()
                .id(1L)
                .productName(new ProductName("Test Product"))
                .productDescription(new ProductDescription("This is a test product description with sufficient length"))
                .productPrice(new ProductPrice(
                        new Amount(new BigDecimal("99.99")),
                        new CurrencyCode("USD")
                ))
                .stockQuantity(new StockQuantity(100))
                .categories(Collections.singletonList(createTestCategory()))
                .imageList(Collections.singletonList(createTestImage(1L)))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("test-user")
                .lastModifiedBy("test-user")
                .build();
    }

    /**
     * Creates a valid ProductCreateDTO with default values
     */
    public static ProductCreateDTO createValidProductCreateDTO() {
        return new ProductCreateDTO(
                "Test Product",
                "This is a test product description with sufficient length",
                new BigDecimal("99.99"),
                "USD",
                100,
                Collections.singletonList(1L),
                Collections.emptyList()
        );
    }

    /**
     * Creates a valid ProductUpdateDTO with default values
     */
    public static ProductUpdateDTO createValidProductUpdateDTO() {
        return new ProductUpdateDTO(
                "Updated Test Product",
                "This is an updated test product description with sufficient length",
                new BigDecimal("129.99"),
                "USD",
                50,
                Collections.singletonList(1L),
                Collections.emptyList()
        );
    }

    /**
     * Creates a valid ProductResponseDTO with default values
     */
    public static ProductResponseDTO createValidProductResponseDTO() {
        return new ProductResponseDTO(
                1L,
                "Test Product",
                "This is a test product description with sufficient length",
                new BigDecimal("99.99"),
                100,
                Collections.singletonList(createTestCategoryResponseDTO()),
                Collections.singletonList(createTestImageDTO())
        );
    }

    /**
     * Creates a test Category entity
     */
    public static Category createTestCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        category.setCreatedBy("test-user");
        category.setLastModifiedBy("test-user");
        return category;
    }

    /**
     * Creates a test CategoryResponseDTO
     */
    public static CategoryResponseDTO createTestCategoryResponseDTO() {
        return new CategoryResponseDTO(1L, "Test Category");
    }

    /**
     * Creates a test Image entity
     */
    public static Image createTestImage(Long productId) {
        Image image = new Image();
        image.setId(1L);
        image.setPath("test-image.jpg");
        image.setMimeType("image/jpeg");
        image.setAltText("Test Image");

        // Create a product with just the ID to avoid circular references
        Product product = new Product();
        product.setId(productId);
        image.setProduct(product);

        image.setCreatedAt(LocalDateTime.now());
        image.setUpdatedAt(LocalDateTime.now());
        return image;
    }

    /**
     * Creates a test ImageDTO
     */
    public static ImageDTO createTestImageDTO() {
        return new ImageDTO(
                1L,
                "test-image.jpg",
                "Test Image",
                "image/jpeg"
        );
    }

    /**
     * Creates a test ProductSearchCriteria
     */
    public static ProductSearchCriteria createTestProductSearchCriteria() {
        return new ProductSearchCriteria(
                "test",
                "1",
                new BigDecimal("10.00"),
                new BigDecimal("100.00"),
                10,
                100
        );
    }
}
