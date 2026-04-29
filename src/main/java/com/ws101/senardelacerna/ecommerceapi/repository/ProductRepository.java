package com.ws101.senardelacerna.ecommerceapi.repository;

import com.ws101.senardelacerna.ecommerceapi.entity.Product;
import com.ws101.senardelacerna.ecommerceapi.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for Product entity.
 * Extends JpaRepository to provide built-in CRUD operations.
 * 
 * <p><b>Custom Query Methods (Method Naming):</b></p>
 * <ul>
 *   <li>{@code findByCategoryName(String)} - Find products by category name</li>
 *   <li>{@code findByPriceBetween(BigDecimal, BigDecimal)} - Price range finder</li>
 *   <li>{@code findByNameContainingIgnoreCase(String)} - Name search (case-insensitive)</li>
 *   <li>{@code findByStockQuantityLessThan(Integer)} - Low stock alerts</li>
 * </ul>
 * 
 * <p><b>Custom JPQL Queries (@Query):</b></p>
 * <ul>
 *   <li>{@code findProductsInPriceRange} - Products within price range</li>
 *   <li>{@code findProductsByCategoryName} - Products by category (JPQL)</li>
 *   <li>{@code findExpensiveProducts} - Products above price threshold</li>
 *   <li>{@code countByCategoryName} - Count products per category</li>
 * </ul>
 * 
 * @author senardelacerna
 * @version 1.0
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ==================== Method Naming Queries ====================
    
    /**
     * Find all products belonging to a specific category.
     * Uses Spring Data JPA's method naming convention.
     * 
     * @param name the category name
     * @return list of products in the specified category
     */
    List<Product> findByCategoryName(String name);

    /**
     * Find products within a price range.
     * 
     * @param minPrice minimum price (inclusive)
     * @param maxPrice maximum price (inclusive)
     * @return list of products within the price range
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Find products whose name contains the specified string (case-insensitive).
     * 
     * @param name the search string
     * @return list of products with matching names
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Find products with stock quantity less than the specified threshold.
     * Useful for low stock alerts.
     * 
     * @param threshold the stock quantity threshold
     * @return list of products with low stock
     */
    List<Product> findByStockQuantityLessThan(Integer threshold);

    // ==================== JPQL Custom Queries ====================

    /**
     * Find products within a price range using JPQL.
     * 
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return list of products within the price range
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findProductsInPriceRange(
            @Param("minPrice") BigDecimal minPrice, 
            @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Find products by category name using JPQL.
     * Demonstrates JOIN between Product and Category.
     * 
     * @param categoryName the category name
     * @return list of products in the specified category
     */
    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.name = :categoryName")
    List<Product> findProductsByCategoryName(@Param("categoryName") String categoryName);

    /**
     * Find products with price above a threshold.
     * 
     * @param price the price threshold
     * @return list of expensive products
     */
    @Query("SELECT p FROM Product p WHERE p.price > :price ORDER BY p.price DESC")
    List<Product> findExpensiveProducts(@Param("price") BigDecimal price);

    /**
     * Count products by category name.
     * 
     * @param categoryName the category name
     * @return count of products in the category
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.name = :categoryName")
    long countByCategoryName(@Param("categoryName") String categoryName);
}