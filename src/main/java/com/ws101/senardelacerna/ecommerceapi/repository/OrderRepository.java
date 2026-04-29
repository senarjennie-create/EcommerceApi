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
 *   <li>{@code findByCustomerEmail(String)} - Find orders by customer email</li>
 *   <li>{@code findByStatus(Order.OrderStatus)} - Find orders by status</li>
 *   <li>{@code findByOrderDateBetween(LocalDateTime, LocalDateTime)} - Find orders in date range</li>
 * </ul>
 * 
 * @author senardelacerna
 * @version 1.0
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find all orders for a specific customer by email.
     * Uses Spring Data JPA method naming convention.
     * 
     * @param email the customer email
     * @return list of orders for the customer
     */
    List<Order> findByCustomerEmail(String email);

    /**
     * Find all orders with a specific status.
     * Uses Spring Data JPA method naming convention.
     * 
     * @param status the order status
     * @return list of orders with the given status
     */
    List<Order> findByStatus(Order.OrderStatus status);

    /**
     * Find orders placed within a specific date range.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of orders in the date range
     */
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find orders by customer name.
     * 
     * @param customerName the customer name
     * @return list of orders for the customer
     */
    List<Order> findByCustomerNameContainingIgnoreCase(String customerName);

    // ==================== Custom JPQL Queries ====================

    /**
     * Find orders with total amount above a threshold.
     * Uses JPQL.
     * 
     * <p><b>JPQL Query:</b></p>
     * <pre>SELECT o FROM Order o WHERE o.totalAmount > :amount</pre>
     * 
     * @param amount the minimum total amount
     * @return list of high-value orders
     */
    @Query("SELECT o FROM Order o WHERE o.totalAmount > :amount ORDER BY o.totalAmount DESC")
    List<Order> findHighValueOrders(@Param("amount") java.math.BigDecimal amount);

    /**
     * Find recent orders (last N days).
     * 
     * <p><b>JPQL Query:</b></p>
 * <pre>SELECT o FROM Order o WHERE o.orderDate > :date</pre>
     * 
     * @param date the cutoff date
     * @return list of recent orders
     */
    @Query("SELECT o FROM Order o WHERE o.orderDate > :date ORDER BY o.orderDate DESC")
    List<Order> findRecentOrders(@Param("date") LocalDateTime date);

    /**
     * Count orders by status.
     * 
     * <p><b>JPQL Query:</b></p>
     * <pre>SELECT COUNT(o) FROM Order o WHERE o.status = :status</pre>
     * 
     * @param status the order status
     * @return count of orders with the given status
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") Order.OrderStatus status);
}