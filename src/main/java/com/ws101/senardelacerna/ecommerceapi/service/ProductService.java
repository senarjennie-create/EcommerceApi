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

/**
 * Service class for product-related operations.
 * Uses Spring Data JPA Repository for database operations.
 * 
 * <p>Key changes from ArrayList-based implementation:</p>
 * <ul>
 *   <li>Injects ProductRepository instead of using manual ArrayList</li>
 *   <li>Uses repository methods for all CRUD operations</li>
 *   <li>Adds @Transactional for database operations</li>
 *   <li>Initializes sample data on first load if database is empty</li>
 * </ul>
 * 
 * @author senardelacerna
 */
@Service
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
        
        // Initialize sample data on first load if empty
        if (productRepository.count() == 0) {
            initializeSampleData();
        }
    }

    /**
     * Initialize sample data for testing.
     * Creates categories and products if database is empty.
     */
    @Transactional
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

        // Sample data (10 products)
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
     * Uses repository's findAll() method.
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Get product by ID.
     * Uses repository's findById() method.
     */
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    /**
     * Create new product.
     * Uses repository's save() method.
     */
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Update product (PUT).
     */
    @Transactional
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
     */
    @Transactional
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
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    /**
     * Filter products by type and value.
     * Uses custom repository query methods.
     */
    @Transactional(readOnly = true)
    public List<Product> filterProducts(String type, String value) {

        switch (type.toLowerCase()) {
            case "category":
                return productRepository.findProductsByCategoryName(value);

            case "name":
                return productRepository.findByNameContainingIgnoreCase(value);

            case "price":
                double price = Double.parseDouble(value);
                return productRepository.findByPriceBetween(BigDecimal.ZERO, BigDecimal.valueOf(price));

            default:
                return productRepository.findAll();
        }
    }

    // ==================== Additional Repository Methods ====================

    /**
     * Find products within a price range.
     */
    @Transactional(readOnly = true)
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findProductsInPriceRange(minPrice, maxPrice);
    }

    /**
     * Find products with low stock.
     */
    @Transactional(readOnly = true)
    public List<Product> findLowStockProducts(Integer threshold) {
        return productRepository.findByStockQuantityLessThan(threshold);
    }

    /**
     * Find expensive products.
     */
    @Transactional(readOnly = true)
    public List<Product> findExpensiveProducts(BigDecimal price) {
        return productRepository.findExpensiveProducts(price);
    }

    /**
     * Count products by category.
     */
    @Transactional(readOnly = true)
    public long countByCategory(String categoryName) {
        return productRepository.countByCategoryName(categoryName);
    }
}