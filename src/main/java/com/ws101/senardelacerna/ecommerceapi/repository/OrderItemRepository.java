package com.ws101.senardelacerna.ecommerceapi.repository;

import com.ws101.senardelacerna.ecommerceapi.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for OrderItem entity.
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
 *   <li>{@code findByOrderId(Long)} - Find order items by order ID</li>
 *   <li>{@code findByQuantityGreaterThan(Integer)} - Find items with high quantity</li>
 * </ul>
 * 
 * @author senardelacerna
 * @version 1.0
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Find all order items for a specific order.
     * Uses Spring Data JPA method naming convention.
     * 
     * @param orderId the order ID
     * @return list of order items for the order
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * Find order items with quantity greater than a threshold.
     * 
     * @param quantity the minimum quantity
     * @return list of bulk order items
     */
    List<OrderItem> findByQuantityGreaterThan(Integer quantity);

    // ==================== Custom JPQL Queries ====================

    /**
     * Find order items with subtotal above a threshold.
     * Uses JPQL.
     * 
     * <p><b>JPQL Query:</b></p>
     * <pre>SELECT oi FROM OrderItem oi WHERE (oi.price * oi.quantity) > :subtotal</pre>
     * 
     * @param subtotal the minimum subtotal
     * @return list of high-value order items
     */
    @Query("SELECT oi FROM OrderItem oi WHERE (oi.price * oi.quantity) > :subtotal")
    List<OrderItem> findHighValueOrderItems(@Param("subtotal") BigDecimal subtotal);

    /**
     * Find order items by product ID.
     * 
     * <p><b>JPQL Query:</b></p>
     * <pre>SELECT oi FROM OrderItem oi JOIN oi.products p WHERE p.id = :productId</pre>
     * 
     * @param productId the product ID
     * @return list of order items containing the product
     */
    @Query("SELECT oi FROM OrderItem oi JOIN oi.products p WHERE p.id = :productId")
    List<OrderItem> findByProductId(@Param("productId") Long productId);
}