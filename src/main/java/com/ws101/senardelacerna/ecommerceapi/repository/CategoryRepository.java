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
 *   <li>{@code findByName(String)} - Find category by exact name</li>
 *   <li>{@code findByNameContaining(String)} - Find categories by name pattern</li>
 * </ul>
 * 
 * @author senardelacerna
 * @version 1.0
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find a category by its exact name.
     * Uses Spring Data JPA method naming convention.
     * 
     * @param name the category name
     * @return optional containing the category if found
     */
    Optional<Category> findByName(String name);

    /**
     * Find categories whose name contains the specified string (case-insensitive).
     * 
     * @param name the name pattern to search for
     * @return list of categories with matching names
     */
    List<Category> findByNameContainingIgnoreCase(String name);

    /**
     * Check if a category exists by name.
     * 
     * @param name the category name
     * @return true if category exists
     */
    boolean existsByName(String name);

    // ==================== Custom JPQL Queries ====================

    /**
     * Find categories that have products with price above a threshold.
     * Uses JPQL with a subquery.
     * 
     * <p><b>JPQL Query:</b></p>
     * <pre>SELECT DISTINCT c FROM Category c JOIN c.products p WHERE p.price > :price</pre>
     * 
     * @param price the minimum price threshold
     * @return list of categories with expensive products
     */
    @Query("SELECT DISTINCT c FROM Category c JOIN c.products p WHERE p.price > :price")
    List<Category> findCategoriesWithExpensiveProducts(@Param("price") java.math.BigDecimal price);
}