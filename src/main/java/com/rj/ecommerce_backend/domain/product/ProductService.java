package com.rj.ecommerce_backend.domain.product;

import com.rj.ecommerce_backend.domain.product.dtos.ProductCreateDTO;
import com.rj.ecommerce_backend.domain.product.dtos.ProductResponseDTO;
import com.rj.ecommerce_backend.domain.product.dtos.ProductUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    public ProductResponseDTO createProduct(ProductCreateDTO productDTO, List<MultipartFile> images);

    Optional<ProductResponseDTO> getProductById(Long id);

    Optional<Product> getProductEntityForValidation(Long productId);

    Page<ProductResponseDTO> getAllProducts(Pageable pageable);

    ProductResponseDTO updateProduct(Long id, ProductUpdateDTO productDTO, List<MultipartFile> newImages);

    void reduceProductQuantity(Long productId, int newQuantity);

    void deleteProduct(Long id);

    Page<ProductResponseDTO> findProductsByCategory(Long categoryId, Pageable pageable);

    Page<ProductResponseDTO> searchProductsByName(String productName, Pageable pageable);

    void deleteProductImage(Long productId, Long productImageId);
}

