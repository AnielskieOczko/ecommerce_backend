package com.rj.ecommerce_backend.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rj.ecommerce_backend.product.dtos.CategoryResponseDTO;
import com.rj.ecommerce_backend.product.dtos.CategorySearchCriteria;
import com.rj.ecommerce_backend.product.service.CategoryService;
import com.rj.ecommerce_backend.sorting.CategorySortField;
import com.rj.ecommerce_backend.sorting.SortValidator;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PublicCategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @Mock
    private SortValidator sortValidator;

    @InjectMocks
    private PublicCategoryController publicCategoryController;

    private ObjectMapper objectMapper;
    private CategoryResponseDTO testCategoryResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(publicCategoryController)
                .setControllerAdvice(new CategoryControllerAdvice())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Register modules for LocalDateTime serialization

        // Set up test data
        testCategoryResponseDTO = CategoryTestDataFactory.createTestCategoryResponseDTO();
    }

    @Test
    void getCategoryById_ShouldReturnCategory_WhenCategoryExists() throws Exception {
        // Given
        Long categoryId = 1L;
        when(categoryService.getCategoryById(categoryId)).thenReturn(Optional.of(testCategoryResponseDTO));

        // When & Then
        mockMvc.perform(get("/api/v1/public/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testCategoryResponseDTO.id().intValue())))
                .andExpect(jsonPath("$.name", is(testCategoryResponseDTO.name())));

        verify(categoryService).getCategoryById(categoryId);
    }

    @Test
    void getCategoryById_ShouldReturnNotFound_WhenCategoryDoesNotExist() throws Exception {
        // Given
        Long categoryId = 999L;
        when(categoryService.getCategoryById(categoryId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/public/categories/{id}", categoryId))
                .andExpect(status().isNotFound());

        verify(categoryService).getCategoryById(categoryId);
    }

    @Test
    void getAllCategories_ShouldReturnPageOfCategories() throws Exception {
        // Given
        Page<CategoryResponseDTO> categoryPage = new PageImpl<>(
                Collections.singletonList(testCategoryResponseDTO),
                PageRequest.of(0, 10),
                1
        );

        when(sortValidator.validateAndBuildSort(anyString(), eq(CategorySortField.class)))
                .thenReturn(Sort.by("id").ascending());
        when(categoryService.getAllCategories(any(), any(CategorySearchCriteria.class)))
                .thenReturn(categoryPage);

        // When & Then
        mockMvc.perform(get("/api/v1/public/categories")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id:asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(testCategoryResponseDTO.id().intValue())))
                .andExpect(jsonPath("$.content[0].name", is(testCategoryResponseDTO.name())));

        verify(sortValidator).validateAndBuildSort(anyString(), eq(CategorySortField.class));
        verify(categoryService).getAllCategories(any(), any(CategorySearchCriteria.class));
    }

    @Test
    void getAllCategories_WithSearchParams_ShouldReturnFilteredCategories() throws Exception {
        // Given
        Page<CategoryResponseDTO> categoryPage = new PageImpl<>(
                Collections.singletonList(testCategoryResponseDTO),
                PageRequest.of(0, 10),
                1
        );

        when(sortValidator.validateAndBuildSort(anyString(), eq(CategorySortField.class)))
                .thenReturn(Sort.by("id").ascending());
        when(categoryService.getAllCategories(any(), any(CategorySearchCriteria.class)))
                .thenReturn(categoryPage);

        // When & Then
        mockMvc.perform(get("/api/v1/public/categories")
                .param("search", "Test")
                .param("name", "Test Category")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id:asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(testCategoryResponseDTO.id().intValue())))
                .andExpect(jsonPath("$.content[0].name", is(testCategoryResponseDTO.name())));

        verify(sortValidator).validateAndBuildSort(anyString(), eq(CategorySortField.class));
        verify(categoryService).getAllCategories(any(), any(CategorySearchCriteria.class));
    }

    @Test
    void getCategoryNames_ShouldReturnListOfCategoryNames() throws Exception {
        // Given
        List<String> categoryNames = Arrays.asList("Category 1", "Category 2");
        when(categoryService.getCategoryNames()).thenReturn(categoryNames);

        // When & Then
        mockMvc.perform(get("/api/v1/public/categories/names"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", is("Category 1")))
                .andExpect(jsonPath("$[1]", is("Category 2")));

        verify(categoryService).getCategoryNames();
    }
}
