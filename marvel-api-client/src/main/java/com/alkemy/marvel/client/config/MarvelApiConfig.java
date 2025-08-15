package com.alkemy.marvel.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "marvel.api")
public class MarvelApiConfig {
    private String baseUrl = "https://gateway.marvel.com/v1/public";
    private String publicKey;
    private String privateKey;
    private int timeout = 30000;
    private int maxRetries = 3;
}