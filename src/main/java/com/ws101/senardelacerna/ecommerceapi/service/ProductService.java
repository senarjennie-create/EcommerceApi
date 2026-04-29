package com.ws101.senardelacerna.ecommerceapi.service;

import com.ws101.senardelacerna.ecommerceapi.entity.Category;
import com.ws101.senardelacerna.ecommerceapi.entity.Product;
import com.ws101.senardelacerna.ecommerceapi.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Service class for product-related operations.
 * Handles business logic and in-memory storage.
 * 
 * @author senardelacerna
 */
@Service
public class ProductService {

    private final List<Product> productList = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong();

    public ProductService() {
        // Create sample categories
        Category electronics = new Category();
        electronics.setId(1L);
        electronics.setName("Electronics");
        electronics.setDescription("Electronic devices and accessories");

        Category clothing = new Category();
        clothing.setId(2L);
        clothing.setName("Clothing");
        clothing.setDescription("Apparel and fashion items");

        // Sample data (10 products)
        for (int i = 1; i <= 10; i++) {
            Product product = new Product(
                    "Product " + i,
                    "Description " + i,
                    BigDecimal.valueOf(100.0 * i),
                    10 * i,
                    "image" + i + ".jpg",
                    i % 2 == 0 ? electronics : clothing
            );
            // Use reflection or direct field access for id since it's auto-generated
            try {
                var idField = Product.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(product, (long) i);
            } catch (Exception e) {
                // Ignore - id will be set by JPA
            }
            productList.add(product);
        }
    }

    /**
     * Get all products
     */
    public List<Product> getAllProducts() {
        return productList;
    }

    /**
     * Get product by ID
     */
    public Product getProductById(Long id) {
        return productList.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    /**
     * Create new product
     */
    public Product createProduct(Product product) {
        product.setId(counter.incrementAndGet());
        productList.add(product);
        return product;
    }

    /**
     * Update product (PUT)
     */
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = getProductById(id);

        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setCategory(updatedProduct.getCategory());
        existing.setStockQuantity(updatedProduct.getStockQuantity());
        existing.setImageUrl(updatedProduct.getImageUrl());

        return existing;
    }

    /**
     * Partial update (PATCH)
     */
    public Product patchProduct(Long id, Map<String, Object> updates) {
        Product product = getProductById(id);

        if (updates.containsKey("name"))
            product.setName((String) updates.get("name"));

        if (updates.containsKey("description"))
            product.setDescription((String) updates.get("description"));

        if (updates.containsKey("price"))
            product.setPrice(new BigDecimal(updates.get("price").toString()));

        if (updates.containsKey("category")) {
            // Handle category as either a Category object or a name string
            Object categoryValue = updates.get("category");
            if (categoryValue instanceof Category) {
                product.setCategory((Category) categoryValue);
            }
        }

        if (updates.containsKey("stockQuantity"))
            product.setStockQuantity((Integer) updates.get("stockQuantity"));

        if (updates.containsKey("imageUrl"))
            product.setImageUrl((String) updates.get("imageUrl"));

        return product;
    }

    /**
     * Delete product
     */
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productList.remove(product);
    }

    /**
     * Filter products
     */
    public List<Product> filterProducts(String type, String value) {

        switch (type.toLowerCase()) {
            case "category":
                return productList.stream()
                        .filter(p -> p.getCategory() != null && 
                                     p.getCategory().getName().equalsIgnoreCase(value))
                        .collect(Collectors.toList());

            case "name":
                return productList.stream()
                        .filter(p -> p.getName().toLowerCase().contains(value.toLowerCase()))
                        .collect(Collectors.toList());

            case "price":
                double price = Double.parseDouble(value);
                return productList.stream()
                        .filter(p -> p.getPrice().doubleValue() <= price)
                        .collect(Collectors.toList());

            default:
                return new ArrayList<>();
        }
    }
}