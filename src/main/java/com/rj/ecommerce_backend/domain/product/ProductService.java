package com.rj.ecommerce_backend.domain.product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Product createProduct(Product product);

    Optional<Product> getProductById(Long id);

    List<Product> getAllProducts();

    Product updateProduct(Long id, Product updatedProduct);

    void deleteProduct(Long id);

    List<Product> searchProducts(String keyword); //Example search method
}

