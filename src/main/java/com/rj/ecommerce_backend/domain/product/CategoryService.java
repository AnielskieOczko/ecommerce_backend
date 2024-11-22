package com.rj.ecommerce_backend.domain.product;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Category createProduct(Category category);

    Optional<Category> getProductById(Long id);

    List<Category> getAllCategories();

    Product updateCategory(Long id, Category updatedCategory);

    void deleteCategory(Long id);

}
