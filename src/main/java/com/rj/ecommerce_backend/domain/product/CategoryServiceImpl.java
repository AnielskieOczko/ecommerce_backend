package com.rj.ecommerce_backend.domain.product;

import com.rj.ecommerce_backend.domain.product.dtos.CategoryCreateDTO;
import com.rj.ecommerce_backend.domain.product.dtos.CategoryResponseDTO;
import com.rj.ecommerce_backend.domain.product.dtos.CategoryUpdateDTO;
import com.rj.ecommerce_backend.domain.product.exceptions.CategoryAlreadyExistsException;
import com.rj.ecommerce_backend.domain.product.exceptions.CategoryNotFoundException;
import com.rj.ecommerce_backend.domain.product.exceptions.InvalidCategoryDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Page<CategoryResponseDTO> getAllCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);

        return categories
                .map(this::mapToDTO);
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
