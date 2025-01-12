package com.rj.ecommerce_backend.domain.product;

import com.rj.ecommerce_backend.domain.product.dtos.CategoryCreateDTO;
import com.rj.ecommerce_backend.domain.product.dtos.CategoryResponseDTO;
import com.rj.ecommerce_backend.domain.product.dtos.CategorySearchCriteria;
import com.rj.ecommerce_backend.domain.product.dtos.CategoryUpdateDTO;
import com.rj.ecommerce_backend.domain.product.exceptions.CategoryNotFoundException;
import com.rj.ecommerce_backend.domain.user.SortValidator;
import com.rj.ecommerce_backend.domain.user.dtos.UserResponseDto;
import com.rj.ecommerce_backend.domain.user.dtos.UserSearchCriteria;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/v1/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryController {

    private final CategoryService categoryService;
    private final CategorySortValidator categorySortValidator;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody @Valid CategoryCreateDTO categoryDTO) {
        CategoryResponseDTO createdCategory = categoryService.createCategory(categoryDTO);
        if (createdCategory == null) {    // Check if the name already exists
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.created(URI.create("/api/categories/" + createdCategory.id())).body(createdCategory); // Return created category with location
    }


    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<CategoryResponseDTO>> getAllCategories(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id:asc") String sort
    ) {

        log.info("Received request to retrieve categories with filters. search={}, name={}",
                search, name);

        Sort validatedSort = categorySortValidator.validateAndBuildSort(sort);
        Pageable pageable = PageRequest.of(page, size, validatedSort);
        CategorySearchCriteria criteria = new CategorySearchCriteria(
                search,
                name
        );

        Page<CategoryResponseDTO> categories = categoryService.getAllCategories(pageable, criteria);

        log.info("Successfully retrieved filtered users. Total elements: {}", categories.getTotalElements());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable Long id, @RequestBody @Valid CategoryUpdateDTO categoryDTO) {
        CategoryResponseDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
        if (updatedCategory == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedCategory);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build(); // Return 204 No Content
        } catch (CategoryNotFoundException ex) {  // Handle the custom exception
            return ResponseEntity.notFound().build();
        }
    }

}
