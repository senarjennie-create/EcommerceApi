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
 * Extends JpaRepository to provide built-in CRUD operations.
 * 
 * @author senardelacerna
 * @version 1.0
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // ==================== Method Naming Queries ====================
    
    /**
     * Find all order items for a specific order.
     * 
     * @param orderId the order ID
     * @return list of order items for the order
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * Find all order items containing a specific product.
     * 
     * @param productId the product ID
     * @return list of order items containing the product
     */
    List<OrderItem> findByProductId(Long productId);

    // ==================== JPQL Custom Queries ====================

    /**
     * Find order items with their associated product eagerly loaded.
     * 
     * @param orderId the order ID
     * @return list of order items with product details
     */
    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.product WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderIdWithProduct(@Param("orderId") Long orderId);

    /**
     * Find order items with their associated order eagerly loaded.
     * 
     * @param productId the product ID
     * @return list of order items with order details
     */
    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.order WHERE oi.product.id = :productId")
    List<OrderItem> findByProductIdWithOrder(@Param("productId") Long productId);

    /**
     * Find top selling products based on total quantity sold.
     * 
     * @param limit maximum number of products to return
     * @return list of products with their total sales quantity
     */
    @Query(value = "SELECT p.id, p.name, SUM(oi.quantity) as totalSold " +
           "FROM order_items oi " +
           "JOIN products p ON oi.product_id = p.id " +
           "GROUP BY p.id, p.name " +
           "ORDER BY totalSold DESC " +
           "LIMIT :limit", nativeQuery = true)
    List<Object[]> findTopSellingProducts(@Param("limit") int limit);

    /**
     * Find order items where product price is above a threshold.
     * 
     * @param price the price threshold
     * @return list of order items with expensive products
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.product.price > :price")
    List<OrderItem> findByProductPriceGreaterThan(@Param("price") BigDecimal price);
}