package com.ws101.senardelacerna.ecommerceapi.controller;

import com.ws101.senardelacerna.ecommerceapi.entity.Product;
import com.ws101.senardelacerna.ecommerceapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for Product endpoints.
 * UPDATED: Now works with database-backed ProductService.
 * 
 * <p>All endpoints now persist data to MySQL/PostgreSQL database
 * instead of in-memory storage.</p>
 * 
 * @author senardelacerna
 * @version 2.0
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5500") // Allow frontend access
public class ProductController {

    private final ProductService productService;

    /**
     * GET /api/products - Retrieve all products from database.
     * 
     * @return List of all products
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        log.info("REST request to get all products from database");
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/{id} - Retrieve a single product by ID.
     * 
     * @param id the product ID
     * @return the product
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        log.info("REST request to get product with id: {}", id);
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * POST /api/products - Create a new product in database.
     * 
     * @param product the product to create
     * @return the created product
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        log.info("REST request to create product: {}", product.getName());
        Product savedProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    /**
     * PUT /api/products/{id} - Update an existing product.
     * 
     * @param id the product ID
     * @param product the updated product data
     * @return the updated product
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id, 
            @RequestBody Product product) {
        log.info("REST request to update product with id: {}", id);
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * DELETE /api/products/{id} - Delete a product from database.
     * 
     * @param id the product ID
     * @return no content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("REST request to delete product with id: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Custom Query Endpoints ====================

    /**
     * GET /api/products/category/{categoryName} - Filter products by category.
     * Uses database query instead of manual filtering.
     * 
     * @param categoryName the category name
     * @return list of products in the category
     */
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String categoryName) {
        log.info("REST request to get products by category: {}", categoryName);
        List<Product> products = productService.getProductsByCategoryName(categoryName);
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/search/price - Filter products by price range.
     * Example: /api/products/search/price?min=10.00&max=100.00
     * 
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return list of products in price range
     */
    @GetMapping("/search/price")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        log.info("REST request to get products between price {} and {}", minPrice, maxPrice);
        List<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/search/name - Search products by name.
     * Example: /api/products/search/name?term=laptop
     * 
     * @param term search term
     * @return list of matching products
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<Product>> searchProductsByName(@RequestParam String term) {
        log.info("REST request to search products by name: {}", term);
        List<Product> products = productService.searchProductsByName(term);
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/low-stock - Get low stock products.
     * Example: /api/products/low-stock?threshold=10
     * 
     * @param threshold stock threshold
     * @return list of low stock products
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts(
            @RequestParam(defaultValue = "10") int threshold) {
        log.info("REST request to get products with stock below {}", threshold);
        List<Product> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/count/category - Count products in a category.
     * Example: /api/products/count/category?name=Electronics
     * 
     * @param categoryName category name
     * @return product count
     */
    @GetMapping("/count/category")
    public ResponseEntity<Long> countProductsByCategory(@RequestParam String categoryName) {
        log.info("REST request to count products in category: {}", categoryName);
        long count = productService.getProductCountByCategory(categoryName);
        return ResponseEntity.ok(count);
    }
}