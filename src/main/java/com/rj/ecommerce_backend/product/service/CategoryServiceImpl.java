package com.rj.ecommerce_backend.product.service;

import com.rj.ecommerce_backend.product.domain.Category;
import com.rj.ecommerce_backend.product.dtos.CategoryCreateDTO;
import com.rj.ecommerce_backend.product.dtos.CategoryResponseDTO;
import com.rj.ecommerce_backend.product.dtos.CategorySearchCriteria;
import com.rj.ecommerce_backend.product.dtos.CategoryUpdateDTO;
import com.rj.ecommerce_backend.product.exceptions.CategoryAlreadyExistsException;
import com.rj.ecommerce_backend.product.exceptions.CategoryNotFoundException;
import com.rj.ecommerce_backend.product.exceptions.InvalidCategoryDataException;
import com.rj.ecommerce_backend.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponseDTO createCategory(CategoryCreateDTO categoryDTO) {

        if (categoryDTO.name() == null || categoryDTO.name().isBlank()) {
            throw new InvalidCategoryDataException("Category name cannot be blank.");
        }

        if (categoryRepository.findByName(categoryDTO.name()).isPresent()) {
            throw new CategoryAlreadyExistsException(categoryDTO.name());
        }

        Category category = new Category();
        category.setName(categoryDTO.name());

        Category savedCategory = categoryRepository.save(category);
        return mapToDTO(savedCategory);
    }

    @Override
    public Optional<CategoryResponseDTO> getCategoryById(Long id) {
        return categoryRepository.findById(id).map(this::mapToDTO);
    }

    @Override
    public Page<CategoryResponseDTO> getAllCategories(Pageable pageable, CategorySearchCriteria criteria) {

        Specification<Category> spec = criteria.toSpecification();

        Page<Category> categories = categoryRepository.findAll(spec, pageable);

        return categories
                .map(this::mapToDTO);
    }

    @Override
    public List<String> getCategoryNames() {
        return categoryRepository.findAll().stream()
                .map(Category::getName)
                .toList();
    }

    @Override
    public CategoryResponseDTO updateCategory(Long id, CategoryUpdateDTO updatedCategoryDTO) { // Return Category, not Product
        Category categoryToUpdate = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        categoryToUpdate.setName(updatedCategoryDTO.name());
        return mapToDTO(categoryRepository.save(categoryToUpdate));
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id); // Throw exception if not found
        }
        categoryRepository.deleteById(id);
    }

    // Helper method to map Category to CategoryResponseDTO
    private CategoryResponseDTO mapToDTO(Category category) {
        return new CategoryResponseDTO(category.getId(), category.getName());
    }
}
