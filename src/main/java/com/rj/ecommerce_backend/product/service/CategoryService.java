package com.rj.ecommerce_backend.product.service;

import com.rj.ecommerce_backend.product.dtos.CategoryCreateDTO;
import com.rj.ecommerce_backend.product.dtos.CategoryResponseDTO;
import com.rj.ecommerce_backend.product.dtos.CategorySearchCriteria;
import com.rj.ecommerce_backend.product.dtos.CategoryUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    CategoryResponseDTO createCategory(CategoryCreateDTO categoryDTO);
    Optional<CategoryResponseDTO> getCategoryById(Long id);
    Page<CategoryResponseDTO> getAllCategories(Pageable pageable, CategorySearchCriteria criteria);
    List<String> getCategoryNames();
    CategoryResponseDTO updateCategory(Long id, CategoryUpdateDTO updatedCategoryDTO);
    void deleteCategory(Long id);

}
