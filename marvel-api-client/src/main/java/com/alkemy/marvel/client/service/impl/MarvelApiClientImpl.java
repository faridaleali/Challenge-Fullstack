package com.alkemy.marvel.client.service.impl;

import com.alkemy.marvel.client.config.MarvelApiConfig;
import com.alkemy.marvel.client.dto.CharacterDataWrapper;
import com.alkemy.marvel.client.exception.MarvelApiException;
import com.alkemy.marvel.client.service.MarvelApiClient;
import com.alkemy.marvel.client.utils.MarvelAuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarvelApiClientImpl implements MarvelApiClient {
    
    private final MarvelApiConfig config;
    private final MarvelAuthUtils authUtils;
    private final WebClient webClient;
    
    @Override
    public CharacterDataWrapper getCharacters(Integer offset, Integer limit) {
        log.info("Fetching characters from Marvel API with offset={} and limit={}", offset, limit);
        
        String timestamp = authUtils.generateTimestamp();
        String hash = authUtils.generateHash(timestamp, config.getPrivateKey(), config.getPublicKey());
        
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/characters")
                            .queryParam("ts", timestamp)
                            .queryParam("apikey", config.getPublicKey())
                            .queryParam("hash", hash)
                            .queryParam("limit", limit != null ? limit : 20)
                            .queryParam("offset", offset != null ? offset : 0)
                            .build())
                    .retrieve()
                    .bodyToMono(CharacterDataWrapper.class)
                    .retryWhen(Retry.backoff(config.getMaxRetries(), Duration.ofSeconds(1)))
                    .timeout(Duration.ofMillis(config.getTimeout()))
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error fetching characters from Marvel API: {}", e.getMessage());
            throw new MarvelApiException("Failed to fetch characters", e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching characters: {}", e.getMessage());
            throw new MarvelApiException("Unexpected error occurred", e);
        }
    }
    
    @Override
    public CharacterDataWrapper getCharacterById(Long characterId) {
        log.info("Fetching character by ID={} from Marvel API", characterId);
        
        String timestamp = authUtils.generateTimestamp();
        String hash = authUtils.generateHash(timestamp, config.getPrivateKey(), config.getPublicKey());
        
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/characters/{characterId}")
                            .queryParam("ts", timestamp)
                            .queryParam("apikey", config.getPublicKey())
                            .queryParam("hash", hash)
                            .build(characterId))
                    .retrieve()
                    .bodyToMono(CharacterDataWrapper.class)
                    .retryWhen(Retry.backoff(config.getMaxRetries(), Duration.ofSeconds(1)))
                    .timeout(Duration.ofMillis(config.getTimeout()))
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("Character with ID={} not found", characterId);
                throw new MarvelApiException("Character not found", e);
            }
            log.error("Error fetching character by ID from Marvel API: {}", e.getMessage());
            throw new MarvelApiException("Failed to fetch character", e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching character by ID: {}", e.getMessage());
            throw new MarvelApiException("Unexpected error occurred", e);
        }
    }
    
    @Override
    public CharacterDataWrapper getCharactersByName(String name, Integer offset, Integer limit) {
        log.info("Fetching characters by name={} from Marvel API with offset={} and limit={}", name, offset, limit);
        
        String timestamp = authUtils.generateTimestamp();
        String hash = authUtils.generateHash(timestamp, config.getPrivateKey(), config.getPublicKey());
        
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/characters")
                            .queryParam("ts", timestamp)
                            .queryParam("apikey", config.getPublicKey())
                            .queryParam("hash", hash)
                            .queryParam("nameStartsWith", name)
                            .queryParam("limit", limit != null ? limit : 20)
                            .queryParam("offset", offset != null ? offset : 0)
                            .build())
                    .retrieve()
                    .bodyToMono(CharacterDataWrapper.class)
                    .retryWhen(Retry.backoff(config.getMaxRetries(), Duration.ofSeconds(1)))
                    .timeout(Duration.ofMillis(config.getTimeout()))
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error fetching characters by name from Marvel API: {}", e.getMessage());
            throw new MarvelApiException("Failed to fetch characters by name", e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching characters by name: {}", e.getMessage());
            throw new MarvelApiException("Unexpected error occurred", e);
        }
    }
}