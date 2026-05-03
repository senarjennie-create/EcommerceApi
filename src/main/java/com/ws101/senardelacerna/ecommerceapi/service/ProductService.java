package com.ws101.senardelacerna.ecommerceapi.service;

import com.ws101.senardelacerna.ecommerceapi.dto.CreateProductDto;
import com.ws101.senardelacerna.ecommerceapi.dto.ProductDTO;
import com.ws101.senardelacerna.ecommerceapi.entity.Category;
import com.ws101.senardelacerna.ecommerceapi.entity.Product;
import com.ws101.senardelacerna.ecommerceapi.repository.CategoryRepository;
import com.ws101.senardelacerna.ecommerceapi.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        log.debug("Fetching all products");
        List<Product> products = productRepository.findAll();
        return products.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        log.debug("Fetching product with id: {}", id);
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return convertToDTO(product);
    }
    
    /**
     * Create a new product using CreateProductDto
     * Validates and converts DTO to Entity before saving
     */
    public ProductDTO createProduct(CreateProductDto productDto) {
        log.debug("Creating new product: {}", productDto.getName());
        
        // Convert DTO to Entity
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(BigDecimal.valueOf(productDto.getPrice()));
        product.setStockQuantity(productDto.getStockQuantity());
        product.setImageUrl(productDto.getImageUrl());
        
        // Set category if categoryId is provided
        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + productDto.getCategoryId()));
            product.setCategory(category);
        }
        
        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with id: {}", savedProduct.getId());
        return convertToDTO(savedProduct);
    }
    
    public ProductDTO updateProduct(Long id, Product productDetails) {
        log.debug("Updating product with id: {}", id);
        
        Product existingProduct = productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        
        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setStockQuantity(productDetails.getStockQuantity());
        existingProduct.setImageUrl(productDetails.getImageUrl());
        
        if (productDetails.getCategory() != null && productDetails.getCategory().getId() != null) {
            Category category = categoryRepository.findById(productDetails.getCategory().getId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            existingProduct.setCategory(category);
        }
        
        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }
    
    public void deleteProduct(Long id) {
        log.debug("Deleting product with id: {}", id);
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        log.info("Product deleted successfully with id: {}", id);
    }
    
    // ==================== Custom Query Methods ====================
    
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByCategoryName(String categoryName) {
        List<Product> products = productRepository.findByCategoryName(categoryName);
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByPriceRange(BigDecimal min, BigDecimal max) {
        List<Product> products = productRepository.findByPriceBetween(min, max);
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProductsByName(String searchTerm) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(searchTerm);
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductDTO> getLowStockProducts(int threshold) {
        List<Product> products = productRepository.findByStockQuantityLessThan(threshold);
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public long getProductCountByCategory(String categoryName) {
        return productRepository.countByCategoryName(categoryName);
    }
    
    // ==================== DTO Conversion ====================
    
    private ProductDTO convertToDTO(Product product) {
        ProductDTO.CategoryDTO categoryDTO = null;
        if (product.getCategory() != null) {
            categoryDTO = new ProductDTO.CategoryDTO(
                product.getCategory().getId(),
                product.getCategory().getName()
            );
        }
        
        return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStockQuantity(),
            product.getImageUrl(),
            categoryDTO
        );
    }
}