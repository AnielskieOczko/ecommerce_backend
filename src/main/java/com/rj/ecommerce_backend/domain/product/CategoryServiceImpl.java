package com.rj.ecommerce_backend.domain.product;

import java.util.List;
import java.util.Optional;

public class CategoryServiceImpl implements CategoryService{
    @Override
    public Category createProduct(Category category) {
        return null;
    }

    @Override
    public Optional<Category> getProductById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Category> getAllCategories() {
        return List.of();
    }

    @Override
    public Product updateCategory(Long id, Category updatedCategory) {
        return null;
    }

    @Override
    public void deleteCategory(Long id) {

    }
}
