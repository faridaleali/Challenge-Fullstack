package com.alkemy.marvel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Registration response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    
    private String message;
    private String username;
    private String email;
    private String fullName;
    private Boolean success;
    
    public static RegisterResponse success(String username, String email, String fullName) {
        return RegisterResponse.builder()
                .message("User registered successfully")
                .username(username)
                .email(email)
                .fullName(fullName)
                .success(true)
                .build();
    }
    
    public static RegisterResponse error(String message) {
        return RegisterResponse.builder()
                .message(message)
                .success(false)
                .build();
    }
}