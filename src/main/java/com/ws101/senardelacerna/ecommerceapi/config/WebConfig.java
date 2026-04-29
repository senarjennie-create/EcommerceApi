package com.ws101.senardelacerna.ecommerceapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS Configuration for the E-Commerce API.
 * Allows frontend applications to access the backend API.
 * 
 * <p>This configuration is necessary when the frontend and backend
 * are running on different origins (e.g., different ports).</p>
 * 
 * @author senardelacerna
 * @version 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure CORS mappings for the entire application.
     * 
     * @param registry the CORS registry to configure
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")           // Apply to all API endpoints
                .allowedOrigins(
                    "http://localhost:5500",     // VS Code Live Server
                    "http://localhost:3000",     // React dev server
                    "http://localhost:8080",     // Same-origin (Spring Boot)
                    "http://127.0.0.1:5500",
                    "http://127.0.0.1:3000",
                    "http://127.0.0.1:8080"
                )
                .allowedMethods(
                    "GET",                       // Retrieve data
                    "POST",                      // Create data
                    "PUT",                       // Update data
                    "DELETE",                    // Delete data
                    "OPTIONS",                   // Pre-flight requests
                    "PATCH"                      // Partial updates
                )
                .allowedHeaders(
                    "*"                          // Allow all headers
                )
                .allowCredentials(true)           // Allow cookies/auth headers
                .maxAge(3600);                   // Cache pre-flight response for 1 hour
    }
}