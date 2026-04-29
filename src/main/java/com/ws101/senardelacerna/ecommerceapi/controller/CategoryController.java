package com.ws101.senardelacerna.ecommerceapi.controller;

import com.ws101.senardelacerna.ecommerceapi.entity.Category;
import com.ws101.senardelacerna.ecommerceapi.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Category endpoints.
 * 
 * @author senardelacerna
 * @version 1.0
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5500")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        log.info("REST request to get all categories");
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        log.info("REST request to get category with id: {}", id);
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        log.info("REST request to create category: {}", category.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id, 
            @RequestBody Category category) {
        log.info("REST request to update category with id: {}", id);
        return ResponseEntity.ok(categoryService.updateCategory(id, category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.info("REST request to delete category with id: {}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}