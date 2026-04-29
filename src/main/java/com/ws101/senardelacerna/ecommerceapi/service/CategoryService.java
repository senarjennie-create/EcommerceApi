package com.ws101.senardelacerna.ecommerceapi.service;

import com.ws101.senardelacerna.ecommerceapi.entity.Category;
import com.ws101.senardelacerna.ecommerceapi.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        log.debug("Fetching all categories");
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        log.debug("Fetching category with id: {}", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    }

    public Category createCategory(Category category) {
        log.debug("Creating new category: {}", category.getName());
        
        if (categoryRepository.existsById(category.getName())) {
            throw new DataIntegrityViolationException(
                "Category with name '" + category.getName() + "' already exists"
            );
        }
        
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        log.debug("Updating category with id: {}", id);
        
        Category existingCategory = getCategoryById(id);
        existingCategory.setName(categoryDetails.getName());
        existingCategory.setDescription(categoryDetails.getDescription());
        
        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        log.debug("Deleting category with id: {}", id);
        
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found with id: " + id);
        }
        
        categoryRepository.deleteById(id);
        log.info("Successfully deleted category with id: {}", id);
    }
}