package com.ws101.senardelacerna.ecommerceapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order entity representing customer orders in the e-commerce system.
 * 
 * <p>This entity demonstrates a One-to-Many relationship with OrderItem.
 * A single Order can have multiple OrderItems associated with it.</p>
 * 
 * <p><b>Relationship:</b> One Order → Many OrderItems</p>
 * <ul>
 *   <li>Uses {@code @OneToMany} on the orderItems field to define the one-to-many side</li>
 *   <li>Uses {@code @ManyToOne} on the OrderItem entity to define the many-to-one side</li>
 *   <li>CascadeType.ALL ensures operations cascade to order items</li>
 *   <li>FetchType.LAZY defers loading of order items until explicitly accessed</li>
 * </ul>
 * 
 * <p><b>JPA Annotations used:</b></p>
 * <ul>
 *   <li>{@code @Entity} - Marks this class as a JPA entity</li>
 *   <li>{@code @Table} - Specifies the database table name</li>
 *   <li>{@code @Id} - Marks id as the primary key</li>
 *   <li>{@code @GeneratedValue} - Enables auto-generation of primary key</li>
 *   <li>{@code @Enumerated} - Maps enum to database column</li>
 *   <li>{@code @OneToMany} - Defines one-to-many relationship with OrderItem</li>
 * </ul>
 * 
 * @author senardelacerna
 * @version 1.0
 */
@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    /**
     * Enum representing the possible statuses of an order.
     */
    public enum OrderStatus {
        PENDING,      // Order has been placed but not yet processed
        PROCESSING,  // Order is being processed
        SHIPPED,     // Order has been shipped
        DELIVERED,   // Order has been delivered
        CANCELLED    // Order has been cancelled
    }

    /**
     * Unique identifier for the order.
     * Auto-generated using IDENTITY strategy (auto-increment in MySQL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Customer name who placed the order.
     * Required field, cannot be null.
     */
    @Column(nullable = false)
    private String customerName;

    /**
     * Customer email for contact purposes.
     * Required field, cannot be null.
     */
    @Column(nullable = false)
    private String customerEmail;

    /**
     * Customer shipping address.
     * Required field.
     */
    @Column(nullable = false, length = 500)
    private String shippingAddress;

    /**
     * Current status of the order.
     * Uses @Enumerated to store enum value as String in database.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    /**
     * Timestamp when the order was placed.
     */
    @Column(nullable = false)
    private LocalDateTime orderDate;

    /**
     * Total amount for the order.
     * Uses BigDecimal for precise monetary calculations.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Order items belonging to this order.
     * 
     * <p>This establishes the One-to-Many relationship:</p>
     * <ul>
     *   <li>{@code mappedBy = "order"} - Indicates the order field in OrderItem owns the relationship</li>
     *   <li>{@code cascade = CascadeType.ALL} - Operations cascade to order items</li>
     *   <li>{@code fetch = FetchType.LAZY} - Order items are loaded on-demand</li>
     * </ul>
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * Adds an order item to this order.
     * Helper method to maintain bidirectional relationship.
     * 
     * @param orderItem the order item to add
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    /**
     * Removes an order item from this order.
     * Helper method to maintain bidirectional relationship.
     * 
     * @param orderItem the order item to remove
     */
    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }

    /**
     * Calculates and updates the total amount based on order items.
     */
    public void calculateTotal() {
        this.totalAmount = orderItems.stream()
            .map(item -> item.getPrice().multiply(new java.math.BigDecimal(item.getQuantity())))
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
}