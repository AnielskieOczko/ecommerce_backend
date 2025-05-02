package com.rj.ecommerce_backend.product.mapper;

import com.rj.ecommerce_backend.product.domain.Category;
import com.rj.ecommerce_backend.product.domain.Image;
import com.rj.ecommerce_backend.product.domain.Product;
import com.rj.ecommerce_backend.product.dtos.CategoryResponseDTO;
import com.rj.ecommerce_backend.product.dtos.ImageDTO;
import com.rj.ecommerce_backend.product.dtos.ProductCreateDTO;
import com.rj.ecommerce_backend.product.dtos.ProductResponseDTO;
import com.rj.ecommerce_backend.product.repository.CategoryRepository;
import com.rj.ecommerce_backend.product.valueobject.ProductDescription;
import com.rj.ecommerce_backend.product.valueobject.ProductName;
import com.rj.ecommerce_backend.product.valueobject.ProductPrice;
import com.rj.ecommerce_backend.product.valueobject.StockQuantity;
import com.rj.ecommerce_backend.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductMapperTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductMapper productMapper;

    private Product testProduct;
    private ProductCreateDTO testProductCreateDTO;
    private Category testCategory;
    private Image testImage;

    @BeforeEach
    void setUp() {
        testProduct = ProductTestDataFactory.createValidProduct();
        testProductCreateDTO = ProductTestDataFactory.createValidProductCreateDTO();
        testCategory = ProductTestDataFactory.createTestCategory();
        testImage = ProductTestDataFactory.createTestImage(1L);
    }

    @Test
    void mapToEntity_ShouldMapDTOToEntity() {
        // Given
        when(categoryRepository.findAllById(anyList())).thenReturn(Collections.singletonList(testCategory));

        // When
        Product result = productMapper.mapToEntity(testProductCreateDTO);

        // Then
        assertNotNull(result);
        assertEquals(testProductCreateDTO.name(), result.getProductName().value());
        assertEquals(testProductCreateDTO.description(), result.getProductDescription().value());
        assertEquals(testProductCreateDTO.price(), result.getProductPrice().amount().value());
        assertEquals(testProductCreateDTO.quantity(), result.getStockQuantity().value());
        assertEquals(1, result.getCategories().size());
        assertEquals(testCategory.getId(), result.getCategories().get(0).getId());
    }

    @Test
    void mapToEntity_WithEmptyCategories_ShouldMapDTOToEntity() {
        // Given
        ProductCreateDTO dtoWithoutCategories = new ProductCreateDTO(
                "Test Product",
                "This is a test product description with sufficient length",
                new BigDecimal("99.99"),
                "USD",
                100,
                null,
                null
        );

        // When
        Product result = productMapper.mapToEntity(dtoWithoutCategories);

        // Then
        assertNotNull(result);
        assertEquals(dtoWithoutCategories.name(), result.getProductName().value());
        assertEquals(dtoWithoutCategories.description(), result.getProductDescription().value());
        assertEquals(dtoWithoutCategories.price(), result.getProductPrice().amount().value());
        assertEquals(dtoWithoutCategories.quantity(), result.getStockQuantity().value());
        assertTrue(result.getCategories().isEmpty());
        assertTrue(result.getImageList().isEmpty());
    }

    @Test
    void mapToDTO_ShouldMapEntityToDTO() {
        // Given
        Product product = testProduct;

        // When
        ProductResponseDTO result = productMapper.mapToDTO(product);

        // Then
        assertNotNull(result);
        assertEquals(product.getId(), result.id());
        assertEquals(product.getProductName().value(), result.name());
        assertEquals(product.getProductDescription().value(), result.description());
        assertEquals(product.getProductPrice().amount().value(), result.price());
        assertEquals(product.getStockQuantity().value(), result.quantity());
        assertEquals(1, result.categories().size());
        assertEquals(product.getCategories().get(0).getId(), result.categories().get(0).id());
        assertEquals(product.getCategories().get(0).getName(), result.categories().get(0).name());
        assertEquals(1, result.imageList().size());
        assertEquals(product.getImageList().get(0).getId(), result.imageList().get(0).id());
    }

    @Test
    void mapToDTO_WithNullValues_ShouldHandleGracefully() {
        // Given
        Product product = ProductTestDataFactory.createValidProduct();
        product.setId(1L);

        // When
        ProductResponseDTO result = productMapper.mapToDTO(product);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(product.getProductName().value(), result.name());
        assertEquals(product.getProductDescription().value(), result.description());
        assertEquals(product.getProductPrice().amount().value(), result.price());
        assertEquals(product.getStockQuantity().value(), result.quantity());
        assertEquals(1, result.categories().size());
        assertEquals(1, result.imageList().size());
    }

    @Test
    void mapToCategoryDTO_ShouldMapCategoryToDTO() {
        // Given
        Category category = testCategory;

        // When
        CategoryResponseDTO result = productMapper.mapToCategoryDTO(category);

        // Then
        assertNotNull(result);
        assertEquals(category.getId(), result.id());
        assertEquals(category.getName(), result.name());
    }

    @Test
    void mapToImageDTO_ShouldMapImageToDTO() {
        // Given
        Image image = testImage;

        // When
        ImageDTO result = productMapper.mapToImageDTO(image);

        // Then
        assertNotNull(result);
        assertEquals(image.getId(), result.id());
        assertEquals(image.getPath(), result.path());
        assertEquals(image.getMimeType(), result.mimeType());
        assertEquals(image.getAltText(), result.altText());
    }

    @Test
    void mapToImageEntity_ShouldMapDTOToEntity() {
        // Given
        ImageDTO imageDTO = ProductTestDataFactory.createTestImageDTO();

        // When
        Image result = productMapper.mapToImageEntity(imageDTO);

        // Then
        assertNotNull(result);
        assertEquals(imageDTO.path(), result.getPath());
        assertEquals(imageDTO.mimeType(), result.getMimeType());
        assertEquals(imageDTO.altText(), result.getAltText());
    }
}
