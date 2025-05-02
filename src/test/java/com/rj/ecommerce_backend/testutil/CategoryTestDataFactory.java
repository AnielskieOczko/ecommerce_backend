package com.rj.ecommerce_backend.testutil;

import com.rj.ecommerce_backend.product.domain.Category;
import com.rj.ecommerce_backend.product.dtos.CategoryCreateDTO;
import com.rj.ecommerce_backend.product.dtos.CategoryResponseDTO;
import com.rj.ecommerce_backend.product.dtos.CategorySearchCriteria;
import com.rj.ecommerce_backend.product.dtos.CategoryUpdateDTO;

import java.time.LocalDateTime;

/**
 * Factory class for creating test category data
 */
public class CategoryTestDataFactory {

    /**
     * Creates a test Category entity with default values
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
     * Creates a test Category entity with specified ID and name
     */
    public static Category createTestCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
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
     * Creates a test CategoryResponseDTO with specified ID and name
     */
    public static CategoryResponseDTO createTestCategoryResponseDTO(Long id, String name) {
        return new CategoryResponseDTO(id, name);
    }

    /**
     * Creates a test CategoryCreateDTO
     */
    public static CategoryCreateDTO createTestCategoryCreateDTO() {
        return new CategoryCreateDTO("Test Category");
    }

    /**
     * Creates a test CategoryCreateDTO with specified name
     */
    public static CategoryCreateDTO createTestCategoryCreateDTO(String name) {
        return new CategoryCreateDTO(name);
    }

    /**
     * Creates a test CategoryUpdateDTO
     */
    public static CategoryUpdateDTO createTestCategoryUpdateDTO() {
        return new CategoryUpdateDTO("Updated Test Category");
    }

    /**
     * Creates a test CategoryUpdateDTO with specified name
     */
    public static CategoryUpdateDTO createTestCategoryUpdateDTO(String name) {
        return new CategoryUpdateDTO(name);
    }

    /**
     * Creates a test CategorySearchCriteria
     */
    public static CategorySearchCriteria createTestCategorySearchCriteria() {
        return new CategorySearchCriteria(null, null);
    }

    /**
     * Creates a test CategorySearchCriteria with specified search and name
     */
    public static CategorySearchCriteria createTestCategorySearchCriteria(String search, String name) {
        return new CategorySearchCriteria(search, name);
    }
}
