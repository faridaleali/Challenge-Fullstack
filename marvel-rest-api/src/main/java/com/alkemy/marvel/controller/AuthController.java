package com.alkemy.marvel.controller;

import com.alkemy.marvel.dto.LoginRequest;
import com.alkemy.marvel.dto.LoginResponse;
import com.alkemy.marvel.dto.RegisterRequest;
import com.alkemy.marvel.dto.RegisterResponse;
import com.alkemy.marvel.service.ApiCallLogService;
import com.alkemy.marvel.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Authentication controller for handling login and registration
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@Validated
@Tag(name = "Authentication", description = "Authentication endpoints for login and registration")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ApiCallLogService apiCallLogService;

    @Operation(
        summary = "User login",
        description = "Authenticate user and return JWT token"
    )
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, 
                                   HttpServletRequest request) {
        try {
            log.info("Login attempt for user: {}", loginRequest.getUsername());
            
            LoginResponse response = authService.login(loginRequest);
            
            // Log the API call
            apiCallLogService.logApiCall(request, HttpStatus.OK.value());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.error("Login failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
            
            // Log the failed API call
            apiCallLogService.logApiCall(request, HttpStatus.UNAUTHORIZED.value());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication failed", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during login for user {}: {}", loginRequest.getUsername(), e.getMessage(), e);
            
            // Log the failed API call
            apiCallLogService.logApiCall(request, HttpStatus.INTERNAL_SERVER_ERROR.value());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error", "An unexpected error occurred"));
        }
    }

    @Operation(
        summary = "User registration",
        description = "Register a new user account"
    )
    @ApiResponse(responseCode = "200", description = "Registration successful")
    @ApiResponse(responseCode = "400", description = "Bad request or validation error")
    @ApiResponse(responseCode = "409", description = "User already exists")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest,
                                      HttpServletRequest request) {
        try {
            log.info("Registration attempt for user: {}", registerRequest.getUsername());
            
            RegisterResponse response = authService.register(registerRequest);
            
            HttpStatus status = response.getSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            
            // Log the API call
            apiCallLogService.logApiCall(request, status.value());
            
            return ResponseEntity.status(status).body(response);
            
        } catch (Exception e) {
            log.error("Unexpected error during registration for user {}: {}", registerRequest.getUsername(), e.getMessage(), e);
            
            // Log the failed API call
            apiCallLogService.logApiCall(request, HttpStatus.INTERNAL_SERVER_ERROR.value());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RegisterResponse.error("An unexpected error occurred during registration"));
        }
    }

    /**
     * Error response DTO for consistent error handling
     */
    public static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }
    }
}