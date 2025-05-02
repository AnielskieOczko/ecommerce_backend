package com.rj.ecommerce_backend.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rj.ecommerce_backend.product.dtos.ProductResponseDTO;
import com.rj.ecommerce_backend.product.dtos.ProductSearchCriteria;
import com.rj.ecommerce_backend.product.service.FileStorageService;
import com.rj.ecommerce_backend.product.service.ProductService;
import com.rj.ecommerce_backend.sorting.ProductSortField;
import com.rj.ecommerce_backend.sorting.SortValidator;
import com.rj.ecommerce_backend.testutil.ProductTestDataFactory;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PublicProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private SortValidator sortValidator;

    @InjectMocks
    private PublicProductController publicProductController;

    private ObjectMapper objectMapper;
    private ProductResponseDTO testProductResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(publicProductController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Register modules for LocalDateTime serialization

        // Set up test data
        testProductResponseDTO = ProductTestDataFactory.createValidProductResponseDTO();
    }

    // Note: The getProductById endpoint is not defined in PublicProductController

    @Test
    void getAllProducts_ShouldReturnPageOfProducts() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<ProductResponseDTO> productPage = new PageImpl<>(
                Collections.singletonList(testProductResponseDTO), pageable, 1);

        when(sortValidator.validateAndBuildSort(any(), eq(ProductSortField.class))).thenReturn(Sort.by("id").ascending());
        when(productService.getAllProducts(any(Pageable.class), any(ProductSearchCriteria.class)))
                .thenReturn(productPage);

        // When & Then
        mockMvc.perform(get("/api/v1/public/products")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id:asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(testProductResponseDTO.id().intValue())))
                .andExpect(jsonPath("$.content[0].name", is(testProductResponseDTO.name())))
                .andExpect(jsonPath("$.content[0].description", is(testProductResponseDTO.description())));

        verify(productService, times(1)).getAllProducts(any(Pageable.class), any(ProductSearchCriteria.class));
    }

    // Note: The findProductsByCategory endpoint is not defined in PublicProductController

    // Note: The searchProductsByName endpoint is not defined in PublicProductController

    @Test
    void getProductImage_ShouldReturnImage() throws Exception {
        // Given
        String filename = "test-image.jpg";
        Resource mockResource = mock(Resource.class);

        when(fileStorageService.loadFileAsResource(anyString())).thenReturn(mockResource);

        // When & Then
        mockMvc.perform(get("/api/v1/public/products/images/{filename}", filename))
                .andExpect(status().isOk());

        verify(fileStorageService, times(1)).loadFileAsResource(filename);
    }
}
