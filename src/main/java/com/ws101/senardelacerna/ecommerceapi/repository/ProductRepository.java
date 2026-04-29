package com.ws101.senardelacerna.ecommerceapi.repository;

import com.ws101.senardelacerna.ecommerceapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ==================== Method Naming Queries ====================
    
    /**
     * Find products by category name
     */
    List<Product> findByCategoryName(String name);
    
    /**
     * Find products by price between min and max
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Find products by name containing (case insensitive)
     */
    List<Product> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find products with stock less than threshold
     */
    List<Product> findByStockQuantityLessThan(Integer threshold);
    
    // ==================== JPQL Queries ====================
    
    /**
     * Find products by price range using JPQL
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max")
    List<Product> findProductsByPriceRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);
    
    /**
     * Find products by category name using JPQL
     */
    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.name = :categoryName")
    List<Product> findProductsByCategoryName(@Param("categoryName") String categoryName);
    
    /**
     * Count products by category name
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.name = :categoryName")
    long countByCategoryName(@Param("categoryName") String categoryName);
    
    /**
     * Find top selling products (native query example)
     */
    @Query(value = "SELECT p.* FROM products p ORDER BY p.price DESC LIMIT :limit", nativeQuery = true)
    List<Product> findTopExpensiveProducts(@Param("limit") int limit);
}