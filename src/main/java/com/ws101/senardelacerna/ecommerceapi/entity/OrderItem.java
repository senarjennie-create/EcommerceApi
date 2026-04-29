package com.ws101.senardelacerna.ecommerceapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * OrderItem entity representing individual items in an order.
 * 
 * <p>This entity demonstrates:</p>
 * <ul>
 *   <li>One-to-Many relationship with Order (Many OrderItems → One Order)</li>
 *   <li>Many-to-Many relationship with Product (Many OrderItems → Many Products)</li>
 * </ul>
 * 
 * <p><b>Relationships:</b></p>
 * <ul>
 *   <li>Many OrderItems → One Order (via order field)</li>
 *   <li>Many OrderItems → Many Products (via products field)</li>
 * </ul>
 * 
 * <p><b>JPA Annotations used:</b></p>
 * <ul>
 *   <li>{@code @Entity} - Marks this class as a JPA entity</li>
 *   <li>{@code @Table} - Specifies the database table name</li>
 *   <li>{@code @Id} - Marks id as the primary key</li>
 *   <li>{@code @GeneratedValue} - Enables auto-generation of primary key</li>
 *   <li>{@code @ManyToOne} - Defines many-to-one relationship with Order</li>
 *   <li>{@code @ManyToMany} - Defines many-to-many relationship with Product</li>
 *   <li>{@code @JoinTable} - Specifies join table for many-to-many</li>
 * </ul>
 * 
 * @author senardelacerna
 * @version 1.0
 */
@Entity
@Table(name = "order_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    /**
     * Unique identifier for the order item.
     * Auto-generated using IDENTITY strategy (auto-increment in MySQL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Quantity of the product in this order item.
     * Must be a positive integer.
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Price of the product at the time of order.
     * Uses BigDecimal for precise monetary calculations.
     * Stored separately to maintain historical pricing.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Order containing this order item.
     * 
     * <p>This establishes the Many-to-One relationship:</p>
     * <ul>
     *   <li>{@code @ManyToOne} - Defines many order items can belong to one order</li>
     *   <li>{@code @JoinColumn} - Specifies the foreign key column</li>
     *   <li>{@code fetch = FetchType.LAZY} - Order is loaded on-demand</li>
     * </ul>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Products associated with this order item.
     * 
     * <p>This establishes the Many-to-Many relationship:</p>
     * <ul>
     *   <li>{@code @ManyToMany} - Defines many order items can contain many products</li>
     *   <li>{@code @JoinTable} - Specifies the join table configuration</li>
     *   <li>{@code cascade = CascadeType.ALL} - Operations cascade to products</li>
     *   <li>{@code fetch = FetchType.LAZY} - Products are loaded on-demand</li>
     * </ul>
     * 
     * <p><b>Note:</b> This is the inverse side of the relationship defined in Product entity.
     * The Product entity owns the relationship via the {@code product_order_items} table.</p>
     */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
        name = "product_order_items",
        joinColumns = @JoinColumn(name = "order_item_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private java.util.Set<Product> products = new java.util.HashSet<>();

    /**
     * Constructor for creating an order item with essential fields.
     * 
     * @param quantity the quantity of the product
     * @param price    the price at the time of order
     */
    public OrderItem(Integer quantity, BigDecimal price) {
        this.quantity = quantity;
        this.price = price;
    }

    /**
     * Calculates the subtotal for this order item.
     * 
     * @return quantity multiplied by price
     */
    public BigDecimal getSubtotal() {
        return price.multiply(new BigDecimal(quantity));
    }
}