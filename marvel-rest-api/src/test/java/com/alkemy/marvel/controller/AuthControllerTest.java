package com.alkemy.marvel.controller;

import com.alkemy.marvel.dto.LoginRequest;
import com.alkemy.marvel.dto.LoginResponse;
import com.alkemy.marvel.dto.RegisterRequest;
import com.alkemy.marvel.dto.RegisterResponse;
import com.alkemy.marvel.service.ApiCallLogService;
import com.alkemy.marvel.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AuthController
 */
@ExtendWith(MockitoExtension.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private ApiCallLogService apiCallLogService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    private RegisterRequest registerRequest;
    private RegisterResponse registerResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("testuser", "test123");
        loginResponse = new LoginResponse("jwt-token", "testuser", "test@marvel.com", "Test User");
        
        registerRequest = new RegisterRequest("newuser", "newpass123", "new@marvel.com", "New User");
        registerResponse = RegisterResponse.success("newuser", "new@marvel.com", "New User");
    }

    @Test
    void login_Success() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);
        doNothing().when(apiCallLogService).logApiCall(any(), anyInt());

        // When & Then
        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@marvel.com"))
                .andExpect(jsonPath("$.fullName").value("Test User"));

        verify(authService).login(any(LoginRequest.class));
        verify(apiCallLogService).logApiCall(any(), eq(200));
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid username or password"));
        doNothing().when(apiCallLogService).logApiCall(any(), anyInt());

        // When & Then
        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Authentication failed"))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));

        verify(authService).login(any(LoginRequest.class));
        verify(apiCallLogService).logApiCall(any(), eq(401));
    }

    @Test
    void login_EmptyUsername_ReturnsBadRequest() throws Exception {
        // Given
        LoginRequest invalidRequest = new LoginRequest("", "test123");

        // When & Then
        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any(LoginRequest.class));
    }

    @Test
    void login_EmptyPassword_ReturnsBadRequest() throws Exception {
        // Given
        LoginRequest invalidRequest = new LoginRequest("testuser", "");

        // When & Then
        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any(LoginRequest.class));
    }

    @Test
    void register_Success() throws Exception {
        // Given
        when(authService.register(any(RegisterRequest.class))).thenReturn(registerResponse);
        doNothing().when(apiCallLogService).logApiCall(any(), anyInt());

        // When & Then
        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("new@marvel.com"))
                .andExpect(jsonPath("$.fullName").value("New User"));

        verify(authService).register(any(RegisterRequest.class));
        verify(apiCallLogService).logApiCall(any(), eq(200));
    }

    @Test
    void register_UsernameAlreadyExists_ReturnsBadRequest() throws Exception {
        // Given
        RegisterResponse errorResponse = RegisterResponse.error("Username is already taken!");
        when(authService.register(any(RegisterRequest.class))).thenReturn(errorResponse);
        doNothing().when(apiCallLogService).logApiCall(any(), anyInt());

        // When & Then
        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Username is already taken!"));

        verify(authService).register(any(RegisterRequest.class));
        verify(apiCallLogService).logApiCall(any(), eq(400));
    }

    @Test
    void register_InvalidEmail_ReturnsBadRequest() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest("newuser", "newpass123", "invalid-email", "New User");

        // When & Then
        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    void register_ShortUsername_ReturnsBadRequest() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest("ab", "newpass123", "new@marvel.com", "New User");

        // When & Then
        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    void register_ShortPassword_ReturnsBadRequest() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest("newuser", "123", "new@marvel.com", "New User");

        // When & Then
        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    void register_EmptyFullName_ReturnsBadRequest() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest("newuser", "newpass123", "new@marvel.com", "");

        // When & Then
        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    void login_UnexpectedException_ReturnsInternalServerError() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new NullPointerException("Unexpected error"));
        doNothing().when(apiCallLogService).logApiCall(any(), anyInt());

        // When & Then
        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Internal server error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));

        verify(authService).login(any(LoginRequest.class));
        verify(apiCallLogService).logApiCall(any(), eq(500));
    }

    @Test
    void register_UnexpectedException_ReturnsInternalServerError() throws Exception {
        // Given
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new NullPointerException("Database connection error"));
        doNothing().when(apiCallLogService).logApiCall(any(), anyInt());

        // When & Then
        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred during registration"));

        verify(authService).register(any(RegisterRequest.class));
        verify(apiCallLogService).logApiCall(any(), eq(500));
    }
}