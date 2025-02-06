package com.rj.ecommerce_backend.product.service;

import com.rj.ecommerce_backend.product.domain.Product;
import com.rj.ecommerce_backend.product.dtos.ProductCreateDTO;
import com.rj.ecommerce_backend.product.dtos.ProductResponseDTO;
import com.rj.ecommerce_backend.product.dtos.ProductSearchCriteria;
import com.rj.ecommerce_backend.product.dtos.ProductUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    public ProductResponseDTO createProduct(ProductCreateDTO productDTO, List<MultipartFile> images);

    Optional<ProductResponseDTO> getProductById(Long id);

    Optional<Product> getProductEntityForValidation(Long productId);

    Page<ProductResponseDTO> getAllProducts(Pageable pageable, ProductSearchCriteria criteria);

    ProductResponseDTO updateProduct(Long id, ProductUpdateDTO productDTO, List<MultipartFile> newImages);

    void reduceProductQuantity(Long productId, int newQuantity);

    void deleteProduct(Long id);

    Page<ProductResponseDTO> findProductsByCategory(Long categoryId, Pageable pageable);

    Page<ProductResponseDTO> searchProductsByName(String productName, Pageable pageable);

    void deleteProductImage(Long productId, Long productImageId);
}

