package com.ws101.senardelacerna.ecommerceapi.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the REST API.
 * Provides consistent error responses for all controllers.
 * 
 * <p>Handles:</p>
 * <ul>
 *   <li>404 - EntityNotFoundException (Resource not found)</li>
 *   <li>400 - DataIntegrityViolationException, validation errors</li>
 *   <li>500 - Unexpected server errors</li>
 * </ul>
 * 
 * @author senardelacerna
 * @version 1.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle EntityNotFoundException (404) - Resource not found.
     * 
     * @param ex the exception
     * @return 404 response with error details
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Not Found");
        response.put("message", ex.getMessage());
        response.put("path", getPath());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handle DataIntegrityViolationException (400) - Database constraint violation.
     * Examples: Duplicate entry, foreign key constraint, not null constraint.
     * 
     * @param ex the exception
     * @return 400 response with error details
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        
        // Extract meaningful message from constraint violation
        String message = "Database constraint violation";
        if (ex.getMessage().contains("Duplicate entry")) {
            message = "A record with this value already exists";
        } else if (ex.getMessage().contains("foreign key")) {
            message = "Referenced record does not exist";
        } else if (ex.getMessage().contains("not null")) {
            message = "Required field is missing";
        }
        
        response.put("message", message);
        response.put("path", getPath());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle IllegalArgumentException (400) - Invalid business logic.
     * 
     * @param ex the exception
     * @return 400 response with error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", ex.getMessage());
        response.put("path", getPath());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle MethodArgumentNotValidException (400) - Validation annotation failures.
     * 
     * @param ex the exception
     * @return 400 response with validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        
        // Collect all validation errors
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        response.put("message", "Validation failed");
        response.put("errors", errors);
        response.put("path", getPath());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle HttpMessageNotReadableException (400) - Malformed JSON.
     * 
     * @param ex the exception
     * @return 400 response with error details
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMalformedJson(HttpMessageNotReadableException ex) {
        log.warn("Malformed JSON request: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", "Malformed JSON request body");
        response.put("path", getPath());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle MethodArgumentTypeMismatchException (400) - Wrong parameter type.
     * 
     * @param ex the exception
     * @return 400 response with error details
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Type mismatch: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", String.format("Parameter '%s' should be of type %s", 
                ex.getName(), ex.getRequiredType().getSimpleName()));
        response.put("path", getPath());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle generic Exception (500) - Unexpected server errors.
     * 
     * @param ex the exception
     * @return 500 response with error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");
        response.put("message", "An unexpected error occurred. Please try again later.");
        response.put("path", getPath());
        
        // Only include debug info in development
        if (isDevelopmentEnvironment()) {
            response.put("debug", ex.getMessage());
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Helper method to get the current request path.
     * 
     * @return the request path or "unknown"
     */
    private String getPath() {
        try {
            // This requires HttpServletRequest injection, but for simplicity
            // we can return a placeholder. In production, inject HttpServletRequest
            return "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * Check if running in development environment.
     * 
     * @return true if development mode
     */
    private boolean isDevelopmentEnvironment() {
        // You can check active profiles here
        return true; // For development
    }
}