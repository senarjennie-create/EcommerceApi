package com.ws101.senardelacerna.ecommerceapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a product in the e-commerce system.
 * This entity serves as the data model for product-related operations
 * including creation, retrieval, update, and deletion of products.
 * 
 * <p>The Product class uses Lombok annotations to reduce boilerplate code:
 * <ul>
 *   <li>{@code @Data} - Generates getters, setters, toString, equals, and hashCode</li>
 *   <li>{@code @NoArgsConstructor} - Creates a default constructor (required by Spring)</li>
 *   <li>{@code @AllArgsConstructor} - Creates a constructor with all fields for easy object creation</li>
 * </ul>
 * </p>
 * 
 * @author senardelacerna
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    /**
     * Unique identifier for the product.
     * This ID is auto-generated using a counter or UUID strategy
     * since the application uses in-memory storage without a database.
     */
    private Long id;

    /**
     * Product name.
     * Required field with minimum length validation.
     * Cannot be null or empty.
     */
    private String name;

    /**
     * Detailed description of the product.
     * Provides additional information about features, specifications,
     * and usage instructions.
     */
    private String description;

    /**
     * Product price in Philippine Peso (PHP).
     * Must be a positive number greater than zero.
     * Uses double precision for decimal values.
     */
    private double price;

    /**
     * Product classification category.
     * Required field used for filtering and organizing products.
     * Examples: "Electronics", "Clothing", "Books", "Home & Living"
     */
    private String category;

    /**
     * Available quantity of the product in inventory.
     * Must be a non-negative integer (0 or greater).
     * Used for stock management and availability checking.
     */
    private int stockQuantity;

    /**
     * Optional URL link to the product's image.
     * Can be null if no image is provided.
     * Should point to a valid image resource (JPEG, PNG, etc.)
     */
    private String imageUrl;
}