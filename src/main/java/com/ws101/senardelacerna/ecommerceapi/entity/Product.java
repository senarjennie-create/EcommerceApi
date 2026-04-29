package com.ws101.senardelacerna.ecommerceapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Product entity representing products in the e-commerce system.
 * 
 * <p>This entity demonstrates:</p>
 * <ul>
 *   <li>JPA annotations (@Entity, @Table, @Id, @GeneratedValue)</li>
 *   <li>Many-to-One relationship with Category</li>
 * </ul>
 * 
 * <p><b>Relationships:</b></p>
 * <ul>
 *   <li>Many Products → One Category (via category field)</li>
 *   <li>Many Products → Many OrderItems (via orderItems field)</li>
 * </ul>
 * 
 * <p><b>JPA Annotations used:</b></p>
 * <ul>
 *   <li>{@code @Entity} - Marks this class as a JPA entity</li>
 *   <li>{@code @Table} - Specifies the database table name</li>
 *   <li>{@code @Id} - Marks id as the primary key</li>
 *   <li>{@code @GeneratedValue} - Enables auto-generation of primary key</li>
 *   <li>{@code @Column} - Maps field to database column</li>
 *   <li>{@code @ManyToOne} - Defines many-to-one relationship with Category</li>
 *   <li>{@code @ManyToMany} - Defines many-to-many relationship with OrderItem</li>
 *   <li>{@code @JoinTable} - Specifies join table for many-to-many</li>
 * </ul>
 * 
 * @author senardelacerna
 * @version 1.0
 */
@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    /**
     * Unique identifier for the product.
     * Auto-generated using IDENTITY strategy (auto-increment in MySQL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Product name.
     * Required field, cannot be null.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Detailed description of the product.
     * Optional field with maximum length of 1000 characters.
     */
    @Column(length = 1000)
    private String description;

    /**
     * Product price.
     * Required field, must be positive.
     * Uses BigDecimal for precise monetary calculations.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Stock quantity available.
     * Must be non-negative.
     */
    @Column(nullable = false)
    private Integer stockQuantity = 0;

    /**
     * Optional URL link to the product image.
     */
    @Column(length = 500)
    private String imageUrl;

    /**
     * Category associated with this product.
     * 
     * <p>This establishes the Many-to-One relationship:</p>
     * <ul>
     *   <li>{@code @ManyToOne} - Defines many products can belong to one category</li>
     *   <li>{@code @JoinColumn} - Specifies the foreign key column</li>
     *   <li>{@code fetch = FetchType.LAZY} - Category is loaded on-demand</li>
     * </ul>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * Order items containing this product.
     * 
     * <p>This establishes the Many-to-Many relationship:</p>
     * <ul>
     *   <li>{@code @ManyToMany} - Defines many products can be in many order items</li>
     *   <li>{@code @JoinTable} - Specifies the join table configuration</li>
     *   <li>{@code cascade = CascadeType.ALL} - Operations cascade to order items</li>
     *   <li>{@code fetch = FetchType.LAZY} - Order items are loaded on-demand</li>
     * </ul>
     */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
        name = "product_order_items",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "order_item_id")
    )
    private Set<OrderItem> orderItems = new HashSet<>();

    /**
     * Constructor for creating a product with essential fields.
     * 
     * @param name          the product name
     * @param description   the product description
     * @param price         the product price
     * @param stockQuantity the available stock
     */
    public Product(String name, String description, BigDecimal price, Integer stockQuantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    /**
     * Constructor for creating a product with all fields.
     * 
     * @param name          the product name
     * @param description   the product description
     * @param price         the product price
     * @param stockQuantity the available stock
     * @param imageUrl      the image URL
     * @param category      the category
     */
    public Product(String name, String description, BigDecimal price, 
                   Integer stockQuantity, String imageUrl, Category category) {
        this(name, description, price, stockQuantity);
        this.imageUrl = imageUrl;
        this.category = category;
    }
}