package com.alkemy.marvel.config;

import com.alkemy.marvel.entity.User;
import com.alkemy.marvel.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data initializer to create default user on application startup
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createDefaultUser();
    }

    /**
     * Create default user if not exists
     */
    private void createDefaultUser() {
        String defaultUsername = "admin";
        String defaultEmail = "admin@marvel.com";
        
        if (!userRepository.existsByUsername(defaultUsername)) {
            User defaultUser = User.builder()
                    .username(defaultUsername)
                    .password(passwordEncoder.encode("admin123"))
                    .email(defaultEmail)
                    .fullName("Marvel Administrator")
                    .role("ADMIN")
                    .enabled(true)
                    .build();

            userRepository.save(defaultUser);
            log.info("Default admin user created successfully");
            log.info("Username: {}, Email: {}", defaultUsername, defaultEmail);
            log.info("Password: admin123 (please change after first login)");
        } else {
            log.info("Default admin user already exists");
        }

        // Create a regular user for testing
        String testUsername = "testuser";
        String testEmail = "test@marvel.com";
        
        if (!userRepository.existsByUsername(testUsername)) {
            User testUser = User.builder()
                    .username(testUsername)
                    .password(passwordEncoder.encode("test123"))
                    .email(testEmail)
                    .fullName("Test User")
                    .role("USER")
                    .enabled(true)
                    .build();

            userRepository.save(testUser);
            log.info("Test user created successfully");
            log.info("Username: {}, Email: {}", testUsername, testEmail);
            log.info("Password: test123");
        } else {
            log.info("Test user already exists");
        }
    }
}