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
 * 
 * <p>Extends {@link JpaRepository} to provide built-in CRUD operations:</p>
 * <ul>
 *   <li>{@code save(S)} - Save or update an entity</li>
 *   <li>{@code findById(ID)} - Find entity by ID</li>
 *   <li>{@code findAll()} - Find all entities</li>
 *   <li>{@code delete(T)} - Delete an entity</li>
 *   <li>{@code count()} - Count entities</li>
 * </ul>
 * 
 * <p><b>Custom Query Methods:</b></p>
 * <ul>
 *   <li>{@code findByCategory(Category)} - Find products by category (relationship)</li>
 *   <li>{@code findByCategoryName(String)} - Find products by category name (method naming)</li>
 *   <li>{@code findByPriceBetween(BigDecimal, BigDecimal)} - Find products in price range</li>
 *   <li>{@code findByNameContaining(String)} - Find products by name containing keyword</li>
 *   <li>{@code findByStockQuantityLessThan(Integer)} - Find low stock products</li>
 * </ul>
 * 
 * <p><b>Custom JPQL Queries:</b></p>
 * <ul>
 *   <li>{@code findProductsInPriceRange} - Products within a price range using JPQL</li>
 *   <li>{@code findProductsByCategoryName} - Products by category name using JPQL</li>
 * </ul>
 * 
 * @author senardelacerna
 * @version 1.0
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find all products belonging to a specific category.
     * Uses Spring Data JPA method naming convention.
     * 
     * @param category the category to search for
     * @return list of products in the given category
     */
    List<Product> findByCategory(Category category);

    /**
     * Find all products by category name.
     * Uses Spring Data JPA method naming convention.
     * 
     * @param categoryName the name of the category
     * @return list of products in the given category
     */
    List<Product> findByCategoryName(String categoryName);

    /**
     * Find products within a specific price range.
     * Uses Spring Data JPA method naming convention.
     * 
     * @param minPrice the minimum price (inclusive)
     * @param maxPrice the maximum price (inclusive)
     * @return list of products within the price range
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Find products whose name contains the specified string (case-insensitive).
     * 
     * @param name the name pattern to search for
     * @return list of products with matching names
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Find products with stock quantity less than the specified value.
     * Useful for identifying low stock items.
     * 
     * @param quantity the threshold stock quantity
     * @return list of products with low stock
     */
    List<Product> findByStockQuantityLessThan(Integer quantity);

    /**
     * Find products with stock quantity greater than or equal to the specified value.
     * 
     * @param quantity the minimum stock quantity
     * @return list of products with sufficient stock
     */
    List<Product> findByStockQuantityGreaterThanEqual(Integer quantity);

    // ==================== Custom JPQL Queries ====================

    /**
     * Find products within a price range using JPQL.
     * 
     * <p><b>JPQL Query:</b></p>
     * <pre>SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max</pre>
     * 
     * @param min the minimum price
     * @param max the maximum price
     * @return list of products within the price range
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max")
    List<Product> findProductsInPriceRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);

    /**
     * Find products by category name using JPQL.
     * 
     * <p><b>JPQL Query:</b></p>
     * <pre>SELECT p FROM Product p WHERE p.category.name = :categoryName</pre>
     * 
     * @param categoryName the name of the category
     * @return list of products in the given category
     */
    @Query("SELECT p FROM Product p WHERE p.category.name = :categoryName")
    List<Product> findProductsByCategoryName(@Param("categoryName") String categoryName);

    /**
     * Find products with price above a certain threshold using JPQL.
     * 
     * <p><b>JPQL Query:</b></p>
     * <pre>SELECT p FROM Product p WHERE p.price > :price</pre>
     * 
     * @param price the minimum price threshold
     * @return list of expensive products
     */
    @Query("SELECT p FROM Product p WHERE p.price > :price ORDER BY p.price DESC")
    List<Product> findExpensiveProducts(@Param("price") BigDecimal price);

    /**
     * Count products by category name using JPQL.
     * 
     * <p><b>JPQL Query:</b></p>
     * <pre>SELECT COUNT(p) FROM Product p WHERE p.category.name = :categoryName</pre>
     * 
     * @param categoryName the name of the category
     * @return count of products in the category
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.name = :categoryName")
    Long countByCategoryName(@Param("categoryName") String categoryName);
}