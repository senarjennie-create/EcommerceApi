package com.ws101.senardelacerna.ecommerceapi.service;

import com.ws101.senardelacerna.ecommerceapi.entity.Category;
import com.ws101.senardelacerna.ecommerceapi.entity.Product;
import com.ws101.senardelacerna.ecommerceapi.exception.ProductNotFoundException;
import com.ws101.senardelacerna.ecommerceapi.repository.CategoryRepository;
import com.ws101.senardelacerna.ecommerceapi.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for product-related operations.
 * Uses Spring Data JPA Repository for data access instead of manual ArrayList.
 * 
 * <p><b>Key Changes from ArrayList-based implementation:</b></p>
 * <ul>
 *   <li>Injects {@link ProductRepository} instead of using in-memory list</li>
 *   <li>Injects {@link CategoryRepository} for category lookups</li>
 *   <li>Uses repository methods for all CRUD operations</li>
 *   <li>Uses custom query methods for filtering</li>
 * </ul>
 * 
 * @author senardelacerna
 * @version 2.0
 */
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Constructor injection for repositories.
     * 
     * @param productRepository the product repository
     * @param categoryRepository the category repository
     */
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        
        // Initialize sample data if database is empty
        if (productRepository.count() == 0) {
            initializeSampleData();
        }
    }

    /**
     * Initialize sample categories and products for testing.
     */
    private void initializeSampleData() {
        // Create sample categories
        Category electronics = new Category();
        electronics.setName("Electronics");
        electronics.setDescription("Electronic devices and accessories");
        electronics = categoryRepository.save(electronics);

        Category clothing = new Category();
        clothing.setName("Clothing");
        clothing.setDescription("Apparel and fashion items");
        clothing = categoryRepository.save(clothing);

        // Sample products
        for (int i = 1; i <= 10; i++) {
            Product product = new Product(
                    "Product " + i,
                    "Description " + i,
                    BigDecimal.valueOf(100.0 * i),
                    10 * i,
                    "image" + i + ".jpg",
                    i % 2 == 0 ? electronics : clothing
            );
            productRepository.save(product);
        }
    }

    /**
     * Get all products.
     * Uses repository's built-in findAll() method.
     * 
     * @return list of all products
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Get product by ID.
     * Uses repository's findById() method.
     * 
     * @param id the product ID
     * @return the product
     * @throws ProductNotFoundException if product not found
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    /**
     * Create new product.
     * Uses repository's save() method.
     * 
     * @param product the product to create
     * @return the created product
     */
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Update product (PUT).
     * Uses repository's findById() and save() methods.
     * 
     * @param id the product ID
     * @param updatedProduct the updated product data
     * @return the updated product
     * @throws ProductNotFoundException if product not found
     */
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = getProductById(id);

        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setCategory(updatedProduct.getCategory());
        existing.setStockQuantity(updatedProduct.getStockQuantity());
        existing.setImageUrl(updatedProduct.getImageUrl());

        return productRepository.save(existing);
    }

    /**
     * Partial update (PATCH).
     * Updates only the specified fields.
     * 
     * @param id the product ID
     * @param updates the fields to update
     * @return the updated product
     * @throws ProductNotFoundException if product not found
     */
    public Product patchProduct(Long id, Map<String, Object> updates) {
        Product product = getProductById(id);

        if (updates.containsKey("name"))
            product.setName((String) updates.get("name"));

        if (updates.containsKey("description"))
            product.setDescription((String) updates.get("description"));

        if (updates.containsKey("price"))
            product.setPrice(new BigDecimal(updates.get("price").toString()));

        if (updates.containsKey("category")) {
            Object categoryValue = updates.get("category");
            if (categoryValue instanceof Category) {
                product.setCategory((Category) categoryValue);
            } else if (categoryValue instanceof String) {
                // Look up category by name
                Optional<Category> category = categoryRepository.findByName((String) categoryValue);
                category.ifPresent(product::setCategory);
            }
        }

        if (updates.containsKey("stockQuantity"))
            product.setStockQuantity((Integer) updates.get("stockQuantity"));

        if (updates.containsKey("imageUrl"))
            product.setImageUrl((String) updates.get("imageUrl"));

        return productRepository.save(product);
    }

    /**
     * Delete product.
     * Uses repository's deleteById() method.
     * 
     * @param id the product ID
     * @throws ProductNotFoundException if product not found
     */
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    /**
     * Filter products using repository query methods.
     * 
     * <p><b>Available filter types:</b></p>
     * <ul>
     *   <li>{@code category} - Filter by category name (uses findByCategoryName)</li>
     *   <li>{@code name} - Filter by name containing value (uses findByNameContainingIgnoreCase)</li>
     *   <li>{@code price} - Filter by maximum price (uses findByPriceBetween)</li>
     *   <li>{@code priceRange} - Filter by price range (uses findProductsInPriceRange)</li>
     *   <li>{@code lowStock} - Filter by low stock threshold (uses findByStockQuantityLessThan)</li>
     * </ul>
     * 
     * @param type the filter type
     * @param value the filter value
     * @return list of filtered products
     */
    public List<Product> filterProducts(String type, String value) {

        switch (type.toLowerCase()) {
            case "category":
                // Uses method naming convention: findByCategoryName
                return productRepository.findByCategoryName(value);

            case "name":
                // Uses method naming convention: findByNameContainingIgnoreCase
                return productRepository.findByNameContainingIgnoreCase(value);

            case "price":
                // Filter by maximum price
                double maxPrice = Double.parseDouble(value);
                return productRepository.findByPriceBetween(BigDecimal.ZERO, BigDecimal.valueOf(maxPrice));

            case "priceRange":
                // Format: "min,max" - uses @Query JPQL
                String[] range = value.split(",");
                if (range.length == 2) {
                    BigDecimal min = new BigDecimal(range[0].trim());
                    BigDecimal max = new BigDecimal(range[1].trim());
                    return productRepository.findProductsInPriceRange(min, max);
                }
                return List.of();

            case "lowStock":
                // Find low stock products
                Integer threshold = Integer.parseInt(value);
                return productRepository.findByStockQuantityLessThan(threshold);

            default:
                return productRepository.findAll();
        }
    }

    // ==================== Additional Repository-based Methods ====================

    /**
     * Find products in a specific price range.
     * Uses custom JPQL query.
     * 
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return list of products in range
     */
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findProductsInPriceRange(minPrice, maxPrice);
    }

    /**
     * Find expensive products above a price threshold.
     * Uses custom JPQL query.
     * 
     * @param price minimum price threshold
     * @return list of expensive products
     */
    public List<Product> findExpensiveProducts(BigDecimal price) {
        return productRepository.findExpensiveProducts(price);
    }

    /**
     * Find products with low stock.
     * Uses method naming convention.
     * 
     * @param threshold the stock threshold
     * @return list of low stock products
     */
    public List<Product> findLowStockProducts(Integer threshold) {
        return productRepository.findByStockQuantityLessThan(threshold);
    }

    /**
     * Count products in a category.
     * Uses custom JPQL query.
     * 
     * @param categoryName the category name
     * @return count of products
     */
    public Long countByCategory(String categoryName) {
        return productRepository.countByCategoryName(categoryName);
    }
}