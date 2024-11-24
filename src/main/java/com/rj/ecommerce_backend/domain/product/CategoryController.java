package com.rj.ecommerce_backend.domain.product;

import com.rj.ecommerce_backend.domain.product.dtos.CategoryCreateDTO;
import com.rj.ecommerce_backend.domain.product.dtos.CategoryResponseDTO;
import com.rj.ecommerce_backend.domain.product.dtos.CategoryUpdateDTO;
import com.rj.ecommerce_backend.domain.product.exceptions.CategoryNotFoundException;
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
import java.util.Objects;

@RestController
@RequestMapping("api/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

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
    public Page<CategoryResponseDTO> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String[] sort
    ) {

        List<Sort.Order> orders = Arrays.stream(sort)
                .map(s -> {
                    String[] parts = s.split(",");
                    String property = parts[0];

                    // Validate property against allowed values
                    if (!property.equals("id") && !property.equals("name")) {
                        throw new IllegalArgumentException("Invalid sort property: " + property + ". Allowed values are 'id' and 'name'.");
                    }

                    Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("asc")
                            ? Sort.Direction.ASC
                            : Sort.Direction.DESC;

                    return new Sort.Order(direction, property);
                })
                .toList();


        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));

        return categoryService.getAllCategories(pageable);
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
