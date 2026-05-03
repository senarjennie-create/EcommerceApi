package com.ws101.senardelacerna.ecommerceapi.controller;

import com.ws101.senardelacerna.ecommerceapi.dto.CreateProductDto;
import com.ws101.senardelacerna.ecommerceapi.dto.ProductDTO;
import com.ws101.senardelacerna.ecommerceapi.entity.Product;
import com.ws101.senardelacerna.ecommerceapi.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for Product endpoints.
 * Secured with Spring Security annotations
 * 
 * @author senardelacerna
 * @version 2.0
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5500", allowCredentials = "true")
public class ProductController {

    private final ProductService productService;

    /**
     * GET /api/products - Retrieve all products from database.
     * PUBLIC ENDPOINT - anyone can view products
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        log.info("GET /api/products - Fetching all products");
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/{id} - Retrieve a single product by ID.
     * PUBLIC ENDPOINT - anyone can view product details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        log.info("GET /api/products/{} - Fetching product", id);
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * POST /api/products - Create a new product.
     * ADMIN ONLY - requires ADMIN role
     */
    @PostMapping
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductDto productDto) {
    log.info("POST /api/products - Creating product: {}", productDto.getName());
    ProductDTO savedProduct = productService.createProduct(productDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
}

    /**
     * PUT /api/products/{id} - Update an existing product.
     * ADMIN ONLY - requires ADMIN role
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        log.info("PUT /api/products/{} - Updating product", id);
        ProductDTO updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * DELETE /api/products/{id} - Delete a product.
     * ADMIN ONLY - requires ADMIN role
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("DELETE /api/products/{} - Deleting product", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/products/category/{categoryName} - Filter products by category.
     * PUBLIC ENDPOINT - anyone can filter products
     */
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable String categoryName) {
        log.info("GET /api/products/category/{} - Filtering by category", categoryName);
        List<ProductDTO> products = productService.getProductsByCategoryName(categoryName);
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/search/price - Filter products by price range.
     * PUBLIC ENDPOINT - anyone can filter by price
     */
    @GetMapping("/search/price")
    public ResponseEntity<List<ProductDTO>> getProductsByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        log.info("GET /api/products/search/price - Price range {} to {}", min, max);
        List<ProductDTO> products = productService.getProductsByPriceRange(min, max);
        return ResponseEntity.ok(products);
    }
}
