package com.rj.ecommerce_backend.product.service;

import com.rj.ecommerce_backend.product.domain.Category;
import com.rj.ecommerce_backend.product.domain.Image;
import com.rj.ecommerce_backend.product.domain.Product;
import com.rj.ecommerce_backend.product.dtos.ProductCreateDTO;
import com.rj.ecommerce_backend.product.dtos.ProductResponseDTO;
import com.rj.ecommerce_backend.product.dtos.ProductSearchCriteria;
import com.rj.ecommerce_backend.product.dtos.ProductUpdateDTO;
import com.rj.ecommerce_backend.product.exceptions.ProductNotFoundException;
import com.rj.ecommerce_backend.product.mapper.ProductMapper;
import com.rj.ecommerce_backend.product.repository.CategoryRepository;
import com.rj.ecommerce_backend.product.repository.ImageRepository;
import com.rj.ecommerce_backend.product.repository.ProductRepository;
import com.rj.ecommerce_backend.product.valueobject.StockQuantity;
import com.rj.ecommerce_backend.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private ProductResponseDTO testProductResponseDTO;
    private ProductCreateDTO testProductCreateDTO;
    private ProductUpdateDTO testProductUpdateDTO;
    private List<MultipartFile> testImages;

    @BeforeEach
    void setUp() {
        // Set up test data
        testProduct = ProductTestDataFactory.createValidProduct();
        testProductResponseDTO = ProductTestDataFactory.createValidProductResponseDTO();
        testProductCreateDTO = ProductTestDataFactory.createValidProductCreateDTO();
        testProductUpdateDTO = ProductTestDataFactory.createValidProductUpdateDTO();
        testImages = Collections.emptyList(); // Mock images list
    }

    @Test
    void createProduct_ShouldCreateAndReturnProduct() {
        // Given
        when(productMapper.mapToEntity(any(ProductCreateDTO.class))).thenReturn(testProduct);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.mapToDTO(any(Product.class))).thenReturn(testProductResponseDTO);

        // When
        ProductResponseDTO result = productService.createProduct(testProductCreateDTO, testImages);

        // Then
        assertNotNull(result);
        assertEquals(testProductResponseDTO, result);
        verify(productRepository).save(any(Product.class));
        verify(productMapper).mapToEntity(testProductCreateDTO);
        verify(productMapper).mapToDTO(testProduct);
    }

    @Test
    void createProduct_WithImages_ShouldStoreImagesAndReturnProduct() {
        // Given
        MultipartFile mockImage = mock(MultipartFile.class);
        List<MultipartFile> images = Collections.singletonList(mockImage);
        Image savedImage = ProductTestDataFactory.createTestImage(1L);

        when(productMapper.mapToEntity(any(ProductCreateDTO.class))).thenReturn(testProduct);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.mapToDTO(any(Product.class))).thenReturn(testProductResponseDTO);
        when(fileStorageService.storeFile(any(MultipartFile.class), anyString(), any(Product.class)))
                .thenReturn(savedImage);

        // When
        ProductResponseDTO result = productService.createProduct(testProductCreateDTO, images);

        // Then
        assertNotNull(result);
        assertEquals(testProductResponseDTO, result);
        verify(productRepository).save(any(Product.class));
        verify(fileStorageService).storeFile(mockImage, "Product Image", testProduct);
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() {
        // Given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productMapper.mapToDTO(testProduct)).thenReturn(testProductResponseDTO);

        // When
        Optional<ProductResponseDTO> result = productService.getProductById(productId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testProductResponseDTO, result.get());
        verify(productRepository).findById(productId);
    }

    @Test
    void getProductById_ShouldReturnEmpty_WhenProductDoesNotExist() {
        // Given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When
        Optional<ProductResponseDTO> result = productService.getProductById(productId);

        // Then
        assertFalse(result.isPresent());
        verify(productRepository).findById(productId);
    }

    @Test
    void getProductEntityForValidation_ShouldReturnProduct_WhenProductExists() {
        // Given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // When
        Optional<Product> result = productService.getProductEntityForValidation(productId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testProduct, result.get());
        verify(productRepository).findById(productId);
    }

    @Test
    void getAllProducts_ShouldReturnPageOfProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        ProductSearchCriteria criteria = ProductTestDataFactory.createTestProductSearchCriteria();
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(testProduct), pageable, 1);

        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(productPage);
        when(productMapper.mapToDTO(testProduct)).thenReturn(testProductResponseDTO);

        // When
        Page<ProductResponseDTO> result = productService.getAllProducts(pageable, criteria);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testProductResponseDTO, result.getContent().get(0));
        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void updateProduct_ShouldUpdateAndReturnProduct_WhenProductExists() {
        // Given
        Long productId = 1L;
        List<Category> categories = Collections.singletonList(ProductTestDataFactory.createTestCategory());

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(categoryRepository.findAllById(anyList())).thenReturn(categories);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.mapToDTO(any(Product.class))).thenReturn(testProductResponseDTO);

        // When
        ProductResponseDTO result = productService.updateProduct(productId, testProductUpdateDTO, testImages);

        // Then
        assertNotNull(result);
        assertEquals(testProductResponseDTO, result);
        verify(productRepository).findById(productId);
        verify(productRepository).save(testProduct);
    }

    @Test
    void updateProduct_ShouldThrowException_WhenProductDoesNotExist() {
        // Given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProductNotFoundException.class, () ->
            productService.updateProduct(productId, testProductUpdateDTO, testImages)
        );
        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void reduceProductQuantity_ShouldUpdateQuantity() {
        // Given
        Long productId = 1L;
        int quantityToReduce = 50;
        int currentStock = 100;

        testProduct.setStockQuantity(new StockQuantity(currentStock));
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // When
        productService.reduceProductQuantity(productId, quantityToReduce);

        // Then
        ArgumentCaptor<StockQuantity> stockQuantityCaptor = ArgumentCaptor.forClass(StockQuantity.class);
        verify(productRepository).updateProductQuantity(eq(productId), stockQuantityCaptor.capture());
        assertEquals(currentStock - quantityToReduce, stockQuantityCaptor.getValue().value());
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() {
        // Given
        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(true);

        // When
        productService.deleteProduct(productId);

        // Then
        verify(productRepository).deleteById(productId);
    }

    @Test
    void findProductsByCategory_ShouldReturnPageOfProducts() {
        // Given
        Long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(testProduct), pageable, 1);

        when(productRepository.findByCategories_Id(categoryId, pageable)).thenReturn(productPage);
        when(productMapper.mapToDTO(testProduct)).thenReturn(testProductResponseDTO);

        // When
        Page<ProductResponseDTO> result = productService.findProductsByCategory(categoryId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testProductResponseDTO, result.getContent().get(0));
        verify(productRepository).findByCategories_Id(categoryId, pageable);
    }

    @Test
    void searchProductsByName_ShouldReturnPageOfProducts() {
        // Given
        String productName = "Test";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(testProduct), pageable, 1);

        when(productRepository.findByProductNameValueContainingIgnoreCase(productName, pageable)).thenReturn(productPage);
        when(productMapper.mapToDTO(testProduct)).thenReturn(testProductResponseDTO);

        // When
        Page<ProductResponseDTO> result = productService.searchProductsByName(productName, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testProductResponseDTO, result.getContent().get(0));
        verify(productRepository).findByProductNameValueContainingIgnoreCase(productName, pageable);
    }

    @Test
    void deleteProductImage_ShouldDeleteImageFromProduct() {
        // Given
        Long productId = 1L;
        Long imageId = 1L;
        Image imageToDelete = ProductTestDataFactory.createTestImage(productId);
        imageToDelete.setId(imageId);

        List<Image> imageList = new ArrayList<>();
        imageList.add(imageToDelete);
        testProduct.setImageList(imageList);

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        productService.deleteProductImage(productId, imageId);

        // Then
        verify(productRepository).findById(productId);
        verify(fileStorageService).deleteImage(imageToDelete);
        verify(productRepository).save(testProduct);
    }
}
