package com.ws101.senardelacerna.ecommerceapi.repository;

import com.ws101.senardelacerna.ecommerceapi.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Category entity.
 * Extends JpaRepository to provide built-in CRUD operations.
 * 
 * <p><b>Custom Query Methods (Method Naming):</b></p>
 * <ul>
 *   <li>{@code findByName(String)} - Find category by exact name</li>
 *   <li>{@code findByNameContainingIgnoreCase(String)} - Name search</li>
 * </ul>
 * 
 * <p><b>Custom JPQL Queries (@Query):</b></p>
 * <ul>
 *   <li>{@code findByNameWithProducts} - Category with products (eager)</li>
 *   <li>{@code findCategoriesWithProductCount} - Categories with product counts</li>
 * </ul>
 * 
 * @author senardelacerna
 * @version 1.0
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // ==================== Method Naming Queries ====================
    
    /**
     * Find a category by exact name (case-insensitive).
     * 
     * @param name the category name
     * @return optional containing the category if found
     */
    Optional<Category> findByNameIgnoreCase(String name);

    /**
     * Find categories whose name contains the specified string.
     * 
     * @param name the search string
     * @return list of matching categories
     */
    List<Category> findByNameContainingIgnoreCase(String name);

    // ==================== JPQL Custom Queries ====================

    /**
     * Find category by name and fetch its products eagerly.
     * Uses JOIN FETCH to avoid N+1 query problem.
     * 
     * @param name the category name
     * @return optional containing the category with products if found
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products WHERE LOWER(c.name) = LOWER(:name)")
    Optional<Category> findByNameWithProducts(@Param("name") String name);

    /**
     * Find all categories with their product counts.
     * Returns categories with at least one product.
     * 
     * @return list of categories with product count > 0
     */
    @Query("SELECT c FROM Category c WHERE SIZE(c.products) > 0")
    List<Category> findCategoriesWithProducts();

    boolean existsById(String name);
}