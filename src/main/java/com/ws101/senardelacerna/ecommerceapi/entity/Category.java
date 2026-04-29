package com.ws101.senardelacerna.ecommerceapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Category entity representing product categories in the e-commerce system.
 * 
 * <p>This entity demonstrates a One-to-Many relationship with the Product entity.
 * A single Category can have multiple Products associated with it.</p>
 * 
 * <p><b>Relationship:</b> One Category → Many Products</p>
 * <ul>
 *   <li>Uses {@code @OneToMany} on the products field to define the one-to-many side</li>
 *   <li>Uses {@code @ManyToOne} on the Product entity to define the many-to-one side</li>
 *   <li>CascadeType.ALL ensures operations cascade to related products</li>
 *   <li>FetchType.LAZY defers loading of products until explicitly accessed</li>
 * </ul>
 * 
 * <p><b>JPA Annotations used:</b></p>
 * <ul>
 *   <li>{@code @Entity} - Marks this class as a JPA entity</li>
 *   <li>{@code @Table} - Specifies the database table name</li>
 *   <li>{@code @Id} - Marks id as the primary key</li>
 *   <li>{@code @GeneratedValue} - Enables auto-generation of primary key</li>
 * </ul>
 * 
 * @author senardelacerna
 * @version 1.0
 */
@Entity
@Table(name = "categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    /**
     * Unique identifier for the category.
     * Auto-generated using IDENTITY strategy (auto-increment in MySQL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the category.
     * Required field, cannot be null.
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Description of the category.
     * Optional field providing additional information about the category.
     */
    @Column(length = 500)
    private String description;

    /**
     * Products belonging to this category.
     * 
     * <p>This establishes the One-to-Many relationship:</p>
     * <ul>
     *   <li>{@code mappedBy = "category"} - Indicates the category field in Product owns the relationship</li>
     *   <li>{@code cascade = CascadeType.ALL} - Operations cascade to products</li>
     *   <li>{@code fetch = FetchType.LAZY} - Products are loaded on-demand</li>
     * </ul>
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    /**
     * Adds a product to this category.
     * Helper method to maintain bidirectional relationship.
     * 
     * @param product the product to add
     */
    public void addProduct(Product product) {
        products.add(product);
        product.setCategory(this);
    }

    /**
     * Removes a product from this category.
     * Helper method to maintain bidirectional relationship.
     * 
     * @param product the product to remove
     */
    public void removeProduct(Product product) {
        products.remove(product);
        product.setCategory(null);
    }
}