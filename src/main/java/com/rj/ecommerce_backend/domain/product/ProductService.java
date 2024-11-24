package com.rj.ecommerce_backend.domain.product;

import com.rj.ecommerce_backend.domain.product.dtos.ProductCreateDTO;
import com.rj.ecommerce_backend.domain.product.dtos.ProductResponseDTO;
import com.rj.ecommerce_backend.domain.product.dtos.ProductUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    ProductResponseDTO createProduct(ProductCreateDTO productDTO);

    Optional<ProductResponseDTO> getProductById(Long id);

    Page<ProductResponseDTO> getAllProducts(Pageable pageable);

    ProductResponseDTO updateProduct(Long id, ProductUpdateDTO productDTO);

    void deleteProduct(Long id);

    Page<ProductResponseDTO> findProductsByCategory(Long categoryId, Pageable pageable);

    Page<ProductResponseDTO> searchProductsByName(String productName, Pageable pageable); //Example search method
}

