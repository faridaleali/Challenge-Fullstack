package com.alkemy.marvel.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Marvel REST API",
        version = "1.0.0",
        description = "REST API para consultar información de personajes de Marvel Comics utilizando la API oficial de Marvel",
        contact = @Contact(
            name = "Marvel API Team",
            email = "support@marvel-api.com"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Local Development Server")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = "JWT Authentication token. Use format: Bearer {token}"
)
public class SwaggerConfig {
    // Configuration is handled by annotations
}