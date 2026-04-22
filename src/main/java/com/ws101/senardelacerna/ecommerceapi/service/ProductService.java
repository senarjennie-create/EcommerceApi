package com.ws101.senardelacerna.ecommerceapi.service;

import com.ws101.senardelacerna.ecommerceapi.exception.ProductNotFoundException;
import com.ws101.senardelacerna.ecommerceapi.model.Product;
import org.springframework.stereotype.Service;

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
        // Sample data (10 products)
        for (int i = 1; i <= 10; i++) {
            productList.add(new Product(
                    counter.incrementAndGet(),
                    "Product " + i,
                    "Description " + i,
                    100.0 * i,
                    i % 2 == 0 ? "Electronics" : "Clothing",
                    10 * i,
                    "image" + i + ".jpg"
            ));
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
            product.setPrice((Double) updates.get("price"));

        if (updates.containsKey("category"))
            product.setCategory((String) updates.get("category"));

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
                        .filter(p -> p.getCategory().equalsIgnoreCase(value))
                        .collect(Collectors.toList());

            case "name":
                return productList.stream()
                        .filter(p -> p.getName().toLowerCase().contains(value.toLowerCase()))
                        .collect(Collectors.toList());

            case "price":
                double price = Double.parseDouble(value);
                return productList.stream()
                        .filter(p -> p.getPrice() <= price)
                        .collect(Collectors.toList());

            default:
                return new ArrayList<>();
        }
    }
}