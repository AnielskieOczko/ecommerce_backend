package com.rj.ecommerce_backend.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rj.ecommerce_backend.product.domain.Product;
import com.rj.ecommerce_backend.product.dtos.ProductCreateDTO;
import com.rj.ecommerce_backend.product.dtos.ProductResponseDTO;
import com.rj.ecommerce_backend.product.dtos.ProductSearchCriteria;
import com.rj.ecommerce_backend.product.dtos.ProductUpdateDTO;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private SortValidator sortValidator;

    @InjectMocks
    private AdminProductController adminProductController;

    private ObjectMapper objectMapper;
    private ProductResponseDTO testProductResponseDTO;
    private ProductCreateDTO testProductCreateDTO;
    private ProductUpdateDTO testProductUpdateDTO;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminProductController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Register modules for LocalDateTime serialization

        // Set up test data
        testProductResponseDTO = ProductTestDataFactory.createValidProductResponseDTO();
        testProductCreateDTO = ProductTestDataFactory.createValidProductCreateDTO();
        testProductUpdateDTO = ProductTestDataFactory.createValidProductUpdateDTO();
        testProduct = ProductTestDataFactory.createValidProduct();
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() throws Exception {
        // Given
        Long productId = 1L;
        when(productService.getProductById(productId)).thenReturn(Optional.of(testProductResponseDTO));

        // When & Then
        mockMvc.perform(get("/api/v1/admin/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testProductResponseDTO.id().intValue())))
                .andExpect(jsonPath("$.name", is(testProductResponseDTO.name())))
                .andExpect(jsonPath("$.description", is(testProductResponseDTO.description())));

        verify(productService, times(1)).getProductById(productId);
    }

    @Test
    void getProductById_ShouldReturnNotFound_WhenProductDoesNotExist() throws Exception {
        // Given
        Long productId = 999L;
        when(productService.getProductById(productId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/admin/products/{id}", productId))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById(productId);
    }

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
        mockMvc.perform(get("/api/v1/admin/products")
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

    @Test
    void createProduct_ShouldCreateAndReturnProduct() throws Exception {
        // Given
        MockMultipartFile productFile = new MockMultipartFile(
                "product",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(testProductCreateDTO)
        );

        MockMultipartFile imageFile = new MockMultipartFile(
                "images",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(productService.createProduct(any(ProductCreateDTO.class), anyList()))
                .thenReturn(testProductResponseDTO);

        // When & Then
        mockMvc.perform(multipart("/api/v1/admin/products")
                .file(productFile)
                .file(imageFile))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testProductResponseDTO.id().intValue())))
                .andExpect(jsonPath("$.name", is(testProductResponseDTO.name())))
                .andExpect(jsonPath("$.description", is(testProductResponseDTO.description())));

        verify(productService, times(1)).createProduct(any(ProductCreateDTO.class), anyList());
    }

    @Test
    void updateProduct_ShouldUpdateAndReturnProduct() throws Exception {
        // Given
        Long productId = 1L;
        MockMultipartFile productFile = new MockMultipartFile(
                "product",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(testProductUpdateDTO)
        );

        MockMultipartFile imageFile = new MockMultipartFile(
                "images",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(productService.updateProduct(eq(productId), any(ProductUpdateDTO.class), anyList()))
                .thenReturn(testProductResponseDTO);

        // When & Then
        mockMvc.perform(multipart("/api/v1/admin/products/{id}", productId)
                .file(productFile)
                .file(imageFile)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testProductResponseDTO.id().intValue())))
                .andExpect(jsonPath("$.name", is(testProductResponseDTO.name())))
                .andExpect(jsonPath("$.description", is(testProductResponseDTO.description())));

        verify(productService, times(1)).updateProduct(eq(productId), any(ProductUpdateDTO.class), anyList());
    }

    @Test
    void deleteProductImage_ShouldDeleteImageAndReturnNoContent() throws Exception {
        // Given
        Long productId = 1L;
        Long imageId = 1L;
        doNothing().when(productService).deleteProductImage(productId, imageId);

        // When & Then
        mockMvc.perform(delete("/api/v1/admin/products/{productId}/images/{imageId}", productId, imageId))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProductImage(productId, imageId);
    }

    @Test
    void deleteProduct_ShouldDeleteProductAndReturnNoContent() throws Exception {
        // Given
        Long productId = 1L;
        doNothing().when(productService).deleteProduct(productId);

        // When & Then
        mockMvc.perform(delete("/api/v1/admin/products/{id}", productId))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(productId);
    }
}
