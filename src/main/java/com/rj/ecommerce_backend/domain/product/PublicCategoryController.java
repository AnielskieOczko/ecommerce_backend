package com.rj.ecommerce_backend.domain.product;

import com.rj.ecommerce_backend.domain.product.dtos.CategoryCreateDTO;
import com.rj.ecommerce_backend.domain.product.dtos.CategoryResponseDTO;
import com.rj.ecommerce_backend.domain.product.dtos.CategorySearchCriteria;
import com.rj.ecommerce_backend.domain.product.dtos.CategoryUpdateDTO;
import com.rj.ecommerce_backend.domain.product.exceptions.CategoryNotFoundException;
import com.rj.ecommerce_backend.domain.user.SortValidator;
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
@RequestMapping("api/v1/public/categories")
@RequiredArgsConstructor
@Slf4j
public class PublicCategoryController {

    private final CategoryService categoryService;
    private final CategorySortValidator categorySortValidator;

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/names")
    public ResponseEntity<List<String>> getCategoriesNames() {
        log.info("Received request to retrieve names of categories.");
        List<String> categoryNames = categoryService.getCategoryNames();

        log.info("Successfully retrieved names of categories. Total elements: {}", categoryNames.size());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryNames);
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

        log.info("Successfully retrieved filtered categories. Total elements: {}", categories.getTotalElements());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categories);
    }


}
