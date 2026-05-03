package com.ws101.senardelacerna.ecommerceapi.controller;

import com.ws101.senardelacerna.ecommerceapi.dto.AuthResponse;
import com.ws101.senardelacerna.ecommerceapi.dto.LoginRequest;
import com.ws101.senardelacerna.ecommerceapi.dto.RegisterRequest;
import com.ws101.senardelacerna.ecommerceapi.entity.Role;
import com.ws101.senardelacerna.ecommerceapi.entity.User;
import com.ws101.senardelacerna.ecommerceapi.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5500", allowCredentials = "true")
public class AuthController {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    
    /**
     * Register a new user
     * Public endpoint - anyone can register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());
        
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                .body(new AuthResponse(null, null, null, null, "Username already taken"));
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                .body(new AuthResponse(null, null, null, null, "Email already registered"));
        }
        
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        
        // Set role based on request or default to USER
        try {
            user.setRole(Role.valueOf(request.getRole().toUpperCase()));
        } catch (IllegalArgumentException e) {
            user.setRole(Role.USER);
        }
        
        user.setEnabled(true);
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new AuthResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                "Registration successful"
            ));
    }
    
    /**
     * Login endpoint - handled by Spring Security's /login
     * This is just for documentation and manual login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            User user = userRepository.findByUsername(request.getUsername()).orElse(null);
            
            if (user != null) {
                return ResponseEntity.ok(new AuthResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().name(),
                    "Login successful"
                ));
            }
        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse(null, null, null, null, "Invalid username or password"));
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new AuthResponse(null, null, null, null, "Login failed"));
    }
    
    /**
     * Get current logged-in user info
     */
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse(null, null, null, null, "Not authenticated"));
        }
        
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        
        if (user != null) {
            return ResponseEntity.ok(new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                "Authenticated"
            ));
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new AuthResponse(null, null, null, null, "User not found"));
    }
}