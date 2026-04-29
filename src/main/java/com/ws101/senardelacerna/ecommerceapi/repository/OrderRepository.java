package com.ws101.senardelacerna.ecommerceapi.repository;

import com.ws101.senardelacerna.ecommerceapi.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entity.
 * Extends JpaRepository to provide built-in CRUD operations.
 * 
 * <p><b>Custom Query Methods (Method Naming):</b></p>
 * <ul>
 *   <li>{@code findByStatus(OrderStatus)} - Find orders by status</li>
 *   <li>{@code findByCustomerEmail(String)} - Find orders by customer email</li>
 *   <li>{@code findByOrderDateBetween(LocalDateTime, LocalDateTime)} - Date range</li>
 * </ul>
 * 
 * <p><b>Custom JPQL Queries (@Query):</b></p>
 * <ul>
 *   <li>{@code findByStatusWithItems} - Orders with items (eager)</li>
 *   <li>{@code findByCustomerNameContaining} - Customer search</li>
 *   <li>{@code findRecentOrders} - Recent orders by date</li>
 * </ul>
 * 
 * @author senardelacerna
 * @version 1.0
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // ==================== Method Naming Queries ====================
    
    /**
     * Find all orders with a specific status.
     * 
     * @param status the order status
     * @return list of orders with the specified status
     */
    List<Order> findByStatus(Order.OrderStatus status);

    /**
     * Find orders by customer email.
     * 
     * @param email the customer email
     * @return list of orders for the customer
     */
    List<Order> findByCustomerEmail(String email);

    /**
     * Find orders placed within a date range.
     * 
     * @param startDate start of date range
     * @param endDate end of date range
     * @return list of orders in the date range
     */
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // ==================== JPQL Custom Queries ====================

    /**
     * Find order by ID and fetch its items eagerly.
     * Uses JOIN FETCH to avoid N+1 query problem.
     * 
     * @param id the order ID
     * @return optional containing the order with items if found
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    /**
     * Find orders by customer name containing the search string.
     * 
     * @param name the customer name search string
     * @return list of matching orders
     */
    @Query("SELECT o FROM Order o WHERE LOWER(o.customerName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Order> findByCustomerNameContaining(@Param("name") String name);

    /**
     * Find recent orders (last N days).
     * 
     * @param date the cutoff date
     * @return list of recent orders
     */
    @Query("SELECT o FROM Order o WHERE o.orderDate >= :date ORDER BY o.orderDate DESC")
    List<Order> findRecentOrders(@Param("date") LocalDateTime date);

    /**
     * Find orders by status with their total amount above threshold.
     * 
     * @param status the order status
     * @param minAmount minimum total amount
     * @return list of orders matching criteria
     */
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.totalAmount >= :minAmount")
    List<Order> findByStatusAndMinAmount(
            @Param("status") Order.OrderStatus status, 
            @Param("minAmount") java.math.BigDecimal minAmount);
}