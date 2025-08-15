package com.alkemy.marvel.service;

import com.alkemy.marvel.dto.LoginRequest;
import com.alkemy.marvel.dto.LoginResponse;
import com.alkemy.marvel.dto.RegisterRequest;
import com.alkemy.marvel.dto.RegisterResponse;
import com.alkemy.marvel.entity.User;
import com.alkemy.marvel.repository.UserRepository;
import com.alkemy.marvel.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication service for handling login and registration
 */
@Slf4j
@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * Authenticate user and generate JWT token
     */
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            String jwt = tokenProvider.generateToken(authentication);
            User user = (User) authentication.getPrincipal();

            log.info("User {} logged in successfully", user.getUsername());
            
            return new LoginResponse(jwt, user.getUsername(), user.getEmail(), user.getFullName());

        } catch (AuthenticationException e) {
            log.error("Authentication failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
            throw new RuntimeException("Invalid username or password");
        }
    }

    /**
     * Register a new user
     */
    public RegisterResponse register(RegisterRequest registerRequest) {
        try {
            // Check if username already exists
            if (userRepository.existsByUsername(registerRequest.getUsername())) {
                return RegisterResponse.error("Username is already taken!");
            }

            // Check if email already exists
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                return RegisterResponse.error("Email is already in use!");
            }

            // Create new user
            User user = User.builder()
                    .username(registerRequest.getUsername())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .email(registerRequest.getEmail())
                    .fullName(registerRequest.getFullName())
                    .role("USER")
                    .enabled(true)
                    .build();

            userRepository.save(user);
            
            log.info("User {} registered successfully", user.getUsername());
            
            return RegisterResponse.success(user.getUsername(), user.getEmail(), user.getFullName());

        } catch (Exception e) {
            log.error("Error registering user {}: {}", registerRequest.getUsername(), e.getMessage());
            return RegisterResponse.error("Error occurred during registration");
        }
    }
}