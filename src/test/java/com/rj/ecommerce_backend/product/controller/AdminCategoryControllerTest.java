package com.rj.ecommerce_backend.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rj.ecommerce_backend.product.dtos.CategoryCreateDTO;
import com.rj.ecommerce_backend.product.dtos.CategoryResponseDTO;
import com.rj.ecommerce_backend.product.dtos.CategorySearchCriteria;
import com.rj.ecommerce_backend.product.dtos.CategoryUpdateDTO;
import com.rj.ecommerce_backend.product.exceptions.CategoryNotFoundException;
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

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminCategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @Mock
    private SortValidator sortValidator;

    @InjectMocks
    private AdminCategoryController adminCategoryController;

    private ObjectMapper objectMapper;
    private CategoryResponseDTO testCategoryResponseDTO;
    private CategoryCreateDTO testCategoryCreateDTO;
    private CategoryUpdateDTO testCategoryUpdateDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminCategoryController)
                .setControllerAdvice(new CategoryControllerAdvice())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Register modules for LocalDateTime serialization

        // Set up test data
        testCategoryResponseDTO = CategoryTestDataFactory.createTestCategoryResponseDTO();
        testCategoryCreateDTO = CategoryTestDataFactory.createTestCategoryCreateDTO();
        testCategoryUpdateDTO = CategoryTestDataFactory.createTestCategoryUpdateDTO();
    }

    @Test
    void createCategory_ShouldCreateAndReturnCategory() throws Exception {
        // Given
        when(categoryService.createCategory(any(CategoryCreateDTO.class))).thenReturn(testCategoryResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategoryCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testCategoryResponseDTO.id().intValue())))
                .andExpect(jsonPath("$.name", is(testCategoryResponseDTO.name())));

        verify(categoryService).createCategory(any(CategoryCreateDTO.class));
    }

    @Test
    void createCategory_ShouldReturnBadRequest_WhenCategoryIsNull() throws Exception {
        // Given
        when(categoryService.createCategory(any(CategoryCreateDTO.class))).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/api/v1/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategoryCreateDTO)))
                .andExpect(status().isBadRequest());

        verify(categoryService).createCategory(any(CategoryCreateDTO.class));
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
        mockMvc.perform(get("/api/v1/admin/categories")
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
    void updateCategory_ShouldUpdateAndReturnCategory() throws Exception {
        // Given
        Long categoryId = 1L;
        when(categoryService.updateCategory(eq(categoryId), any(CategoryUpdateDTO.class)))
                .thenReturn(testCategoryResponseDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/admin/categories/{id}", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategoryUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testCategoryResponseDTO.id().intValue())))
                .andExpect(jsonPath("$.name", is(testCategoryResponseDTO.name())));

        verify(categoryService).updateCategory(eq(categoryId), any(CategoryUpdateDTO.class));
    }

    @Test
    void updateCategory_ShouldReturnNotFound_WhenCategoryDoesNotExist() throws Exception {
        // Given
        Long categoryId = 999L;
        when(categoryService.updateCategory(eq(categoryId), any(CategoryUpdateDTO.class)))
                .thenThrow(new CategoryNotFoundException(categoryId));

        // When & Then
        mockMvc.perform(put("/api/v1/admin/categories/{id}", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategoryUpdateDTO)))
                .andExpect(status().isNotFound());

        verify(categoryService).updateCategory(eq(categoryId), any(CategoryUpdateDTO.class));
    }

    @Test
    void deleteCategory_ShouldDeleteAndReturnNoContent() throws Exception {
        // Given
        Long categoryId = 1L;
        doNothing().when(categoryService).deleteCategory(categoryId);

        // When & Then
        mockMvc.perform(delete("/api/v1/admin/categories/{id}", categoryId))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory(categoryId);
    }

    @Test
    void deleteCategory_ShouldReturnNotFound_WhenCategoryDoesNotExist() throws Exception {
        // Given
        Long categoryId = 999L;
        doThrow(new CategoryNotFoundException(categoryId)).when(categoryService).deleteCategory(categoryId);

        // When & Then
        mockMvc.perform(delete("/api/v1/admin/categories/{id}", categoryId))
                .andExpect(status().isNotFound());

        verify(categoryService).deleteCategory(categoryId);
    }
}
