package com.ws101.senardelacerna.ecommerceapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String message;
    private String token;

    public AuthResponse(Long id, String username, String email, String role, String message) {
        this(id, username, email, role, message, null);
    }

    public AuthResponse(Long id, String username, String email, String role, String message, String token) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.message = message;
        this.token = token;
    }
}
