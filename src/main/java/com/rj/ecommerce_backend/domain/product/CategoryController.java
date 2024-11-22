package com.rj.ecommerce_backend.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Product> createCategory(@RequestBody Category category) {

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category updatedCategory) {

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}
