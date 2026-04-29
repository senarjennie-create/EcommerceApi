package com.ws101.senardelacerna.ecommerceapi.service;

import com.ws101.senardelacerna.ecommerceapi.entity.Category;
import com.ws101.senardelacerna.ecommerceapi.entity.Product;
import com.ws101.senardelacerna.ecommerceapi.repository.CategoryRepository;
import com.ws101.senardelacerna.ecommerceapi.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Service layer for Product business logic.
 * REFACTORED: Now uses JPA Repository instead of ArrayList.
 * 
 * <p>All manual list manipulation has been removed. Data persistence is now
 * handled by Spring Data JPA with automatic database operations.</p>
 * 
 * @author senardelacerna
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    /**
     * Retrieves all products from the database.
     * Replaces the old ArrayList-based method.
     * 
     * @return List of all products
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        log.debug("Fetching all products from database");
        return productRepository.findAll();
    }
    
    /**
     * Retrieves a product by its ID.
     * Uses repository findById() instead of manual list iteration.
     * 
     * @param id the product ID
     * @return the product
     * @throws EntityNotFoundException if product not found
     */
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        log.debug("Fetching product with id: {}", id);
        return productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }
    
    /**
     * Creates a new product in the database.
     * Replaces manual list addition with repository save().
     * 
     * @param product the product to create
     * @return the saved product
     * @throws DataIntegrityViolationException if business rules violated
     */
    public Product createProduct(Product product) {
        log.debug("Creating new product: {}", product.getName());

        validateProduct(product);
        product.setCategory(resolveCategory(product.getCategory()));
        return productRepository.save(product);
    }
    
    /**
     * Updates an existing product.
     * Replaces manual find-and-replace logic with repository operations.
     * 
     * @param id the product ID
     * @param productDetails the updated product data
     * @return the updated product
     * @throws EntityNotFoundException if product not found
     */
    public Product updateProduct(Long id, Product productDetails) {
        log.debug("Updating product with id: {}", id);

        Product existingProduct = getProductById(id);

        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setStockQuantity(productDetails.getStockQuantity());
        existingProduct.setImageUrl(productDetails.getImageUrl());

        existingProduct.setCategory(resolveCategory(productDetails.getCategory()));
        validateProduct(existingProduct);
        return productRepository.save(existingProduct);
    }

    /**
     * Applies partial updates to a product.
     *
     * @param id the product ID
     * @param updates fields to update
     * @return the updated product
     */
    public Product patchProduct(Long id, Map<String, Object> updates) {
        log.debug("Patching product with id: {}", id);

        Product product = getProductById(id);

        if (updates.containsKey("name")) {
            product.setName((String) updates.get("name"));
        }
        if (updates.containsKey("description")) {
            product.setDescription((String) updates.get("description"));
        }
        if (updates.containsKey("price")) {
            product.setPrice(new BigDecimal(updates.get("price").toString()));
        }
        if (updates.containsKey("stockQuantity")) {
            product.setStockQuantity(Integer.valueOf(updates.get("stockQuantity").toString()));
        }
        if (updates.containsKey("imageUrl")) {
            product.setImageUrl((String) updates.get("imageUrl"));
        }
        if (updates.containsKey("categoryId")) {
            Long categoryId = Long.valueOf(updates.get("categoryId").toString());
            Category category = new Category();
            category.setId(categoryId);
            product.setCategory(resolveCategory(category));
        }

        validateProduct(product);
        return productRepository.save(product);
    }
    
    /**
     * Deletes a product by ID.
     * Replaces manual list removal with repository delete().
     * 
     * @param id the product ID
     * @throws EntityNotFoundException if product not found
     */
    public void deleteProduct(Long id) {
        log.debug("Deleting product with id: {}", id);
        
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        
        productRepository.deleteById(id);
        log.info("Successfully deleted product with id: {}", id);
    }
    
    // ==================== Custom Query Methods ====================
    
    /**
     * Finds products by category name.
     * Uses JPA method naming convention (NOT manual filtering).
     * 
     * @param categoryName the category name
     * @return List of products in the category
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategoryName(String categoryName) {
        log.debug("Fetching products in category: {}", categoryName);
        return productRepository.findByCategoryName(categoryName);
    }

    /**
     * Filters products using the query methods supported by the service layer.
     *
     * @param filterType supported values: category, name, lowStock
     * @param filterValue filter input value
     * @return matching products
     */
    @Transactional(readOnly = true)
    public List<Product> filterProducts(String filterType, String filterValue) {
        log.debug("Filtering products by {} with value {}", filterType, filterValue);

        return switch (filterType.toLowerCase()) {
            case "category" -> getProductsByCategoryName(filterValue);
            case "name" -> searchProductsByName(filterValue);
            case "lowstock" -> getLowStockProducts(Integer.parseInt(filterValue));
            default -> throw new IllegalArgumentException("Unsupported filter type: " + filterType);
        };
    }
    
    /**
     * Finds products within a price range.
     * Uses JPQL custom query.
     * 
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return List of products in the price range
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("Fetching products between price {} and {}", minPrice, maxPrice);
        
        if (minPrice.compareTo(BigDecimal.ZERO) < 0 || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Min price cannot be greater than max price");
        }
        
        return productRepository.findProductsInPriceRange(minPrice, maxPrice);
    }
    
    /**
     * Searches products by name (case-insensitive).
     * Uses method naming convention.
     * 
     * @param searchTerm the search term
     * @return List of matching products
     */
    @Transactional(readOnly = true)
    public List<Product> searchProductsByName(String searchTerm) {
        log.debug("Searching products with name containing: {}", searchTerm);
        return productRepository.findByNameContainingIgnoreCase(searchTerm);
    }
    
    /**
     * Gets low stock products for inventory alerts.
     * 
     * @param threshold stock threshold
     * @return List of products with stock below threshold
     */
    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts(int threshold) {
        log.debug("Fetching products with stock below {}", threshold);
        return productRepository.findByStockQuantityLessThan(threshold);
    }
    
    /**
     * Gets count of products in a category.
     * Uses JPQL aggregation query.
     * 
     * @param categoryName the category name
     * @return product count
     */
    @Transactional(readOnly = true)
    public long getProductCountByCategory(String categoryName) {
        log.debug("Counting products in category: {}", categoryName);
        return productRepository.countByCategoryName(categoryName);
    }

    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }

        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }

        if (product.getStockQuantity() == null || product.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
    }

    private Category resolveCategory(Category category) {
        if (category == null || category.getId() == null) {
            return null;
        }

        return categoryRepository.findById(category.getId())
            .orElseThrow(() -> new EntityNotFoundException(
                "Category not found with id: " + category.getId()
            ));
    }
}
