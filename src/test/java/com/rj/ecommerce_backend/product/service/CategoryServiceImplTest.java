package com.rj.ecommerce_backend.product.service;

import com.rj.ecommerce_backend.product.domain.Category;
import com.rj.ecommerce_backend.product.dtos.CategoryCreateDTO;
import com.rj.ecommerce_backend.product.dtos.CategoryResponseDTO;
import com.rj.ecommerce_backend.product.dtos.CategorySearchCriteria;
import com.rj.ecommerce_backend.product.dtos.CategoryUpdateDTO;
import com.rj.ecommerce_backend.product.exceptions.CategoryAlreadyExistsException;
import com.rj.ecommerce_backend.product.exceptions.CategoryNotFoundException;
import com.rj.ecommerce_backend.product.exceptions.InvalidCategoryDataException;
import com.rj.ecommerce_backend.product.repository.CategoryRepository;
import com.rj.ecommerce_backend.testutil.CategoryTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;
    private CategoryResponseDTO testCategoryResponseDTO;
    private CategoryCreateDTO testCategoryCreateDTO;
    private CategoryUpdateDTO testCategoryUpdateDTO;

    @BeforeEach
    void setUp() {
        testCategory = CategoryTestDataFactory.createTestCategory();
        testCategoryResponseDTO = CategoryTestDataFactory.createTestCategoryResponseDTO();
        testCategoryCreateDTO = CategoryTestDataFactory.createTestCategoryCreateDTO();
        testCategoryUpdateDTO = CategoryTestDataFactory.createTestCategoryUpdateDTO();
    }

    @Test
    void createCategory_ShouldCreateAndReturnCategory() {
        // Given
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // When
        CategoryResponseDTO result = categoryService.createCategory(testCategoryCreateDTO);

        // Then
        assertNotNull(result);
        assertEquals(testCategoryResponseDTO.id(), result.id());
        assertEquals(testCategoryResponseDTO.name(), result.name());
        verify(categoryRepository).findByName(testCategoryCreateDTO.name());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_ShouldThrowException_WhenNameIsBlank() {
        // Given
        CategoryCreateDTO invalidDTO = new CategoryCreateDTO("");

        // When & Then
        assertThrows(InvalidCategoryDataException.class, () -> 
            categoryService.createCategory(invalidDTO)
        );
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void createCategory_ShouldThrowException_WhenNameIsNull() {
        // Given
        CategoryCreateDTO invalidDTO = new CategoryCreateDTO(null);

        // When & Then
        assertThrows(InvalidCategoryDataException.class, () -> 
            categoryService.createCategory(invalidDTO)
        );
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void createCategory_ShouldThrowException_WhenCategoryAlreadyExists() {
        // Given
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(testCategory));

        // When & Then
        assertThrows(CategoryAlreadyExistsException.class, () -> 
            categoryService.createCategory(testCategoryCreateDTO)
        );
        verify(categoryRepository).findByName(testCategoryCreateDTO.name());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void getCategoryById_ShouldReturnCategory_WhenCategoryExists() {
        // Given
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));

        // When
        Optional<CategoryResponseDTO> result = categoryService.getCategoryById(categoryId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testCategoryResponseDTO.id(), result.get().id());
        assertEquals(testCategoryResponseDTO.name(), result.get().name());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void getCategoryById_ShouldReturnEmpty_WhenCategoryDoesNotExist() {
        // Given
        Long categoryId = 999L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When
        Optional<CategoryResponseDTO> result = categoryService.getCategoryById(categoryId);

        // Then
        assertFalse(result.isPresent());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void getAllCategories_ShouldReturnPageOfCategories() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        CategorySearchCriteria criteria = CategoryTestDataFactory.createTestCategorySearchCriteria();
        Page<Category> categoryPage = new PageImpl<>(Collections.singletonList(testCategory), pageable, 1);

        when(categoryRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(categoryPage);

        // When
        Page<CategoryResponseDTO> result = categoryService.getAllCategories(pageable, criteria);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testCategoryResponseDTO.id(), result.getContent().get(0).id());
        assertEquals(testCategoryResponseDTO.name(), result.getContent().get(0).name());
        verify(categoryRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getCategoryNames_ShouldReturnAllCategoryNames() {
        // Given
        Category category1 = CategoryTestDataFactory.createTestCategory(1L, "Category 1");
        Category category2 = CategoryTestDataFactory.createTestCategory(2L, "Category 2");
        List<Category> categories = Arrays.asList(category1, category2);

        when(categoryRepository.findAll()).thenReturn(categories);

        // When
        List<String> result = categoryService.getCategoryNames();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("Category 1"));
        assertTrue(result.contains("Category 2"));
        verify(categoryRepository).findAll();
    }

    @Test
    void updateCategory_ShouldUpdateAndReturnCategory_WhenCategoryExists() {
        // Given
        Long categoryId = 1L;
        Category categoryToUpdate = testCategory;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryToUpdate));
        when(categoryRepository.save(any(Category.class))).thenReturn(categoryToUpdate);

        // When
        CategoryResponseDTO result = categoryService.updateCategory(categoryId, testCategoryUpdateDTO);

        // Then
        assertNotNull(result);
        assertEquals(categoryId, result.id());
        assertEquals(testCategoryUpdateDTO.name(), result.name());
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).save(categoryToUpdate);
    }

    @Test
    void updateCategory_ShouldThrowException_WhenCategoryDoesNotExist() {
        // Given
        Long categoryId = 999L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CategoryNotFoundException.class, () -> 
            categoryService.updateCategory(categoryId, testCategoryUpdateDTO)
        );
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_ShouldDeleteCategory_WhenCategoryExists() {
        // Given
        Long categoryId = 1L;
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(categoryId);

        // When
        categoryService.deleteCategory(categoryId);

        // Then
        verify(categoryRepository).existsById(categoryId);
        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    void deleteCategory_ShouldThrowException_WhenCategoryDoesNotExist() {
        // Given
        Long categoryId = 999L;
        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        // When & Then
        assertThrows(CategoryNotFoundException.class, () -> 
            categoryService.deleteCategory(categoryId)
        );
        verify(categoryRepository).existsById(categoryId);
        verify(categoryRepository, never()).deleteById(anyLong());
    }
}
