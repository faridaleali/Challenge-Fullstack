package com.alkemy.marvel.service;

import com.alkemy.marvel.client.dto.Character;
import com.alkemy.marvel.client.dto.CharacterDataWrapper;
import com.alkemy.marvel.client.service.MarvelApiClient;
import com.alkemy.marvel.dto.CharacterResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling Marvel character operations
 */
@Slf4j
@Service
public class CharacterService {

    @Autowired
    private MarvelApiClient marvelApiClient;

    /**
     * Get all characters with pagination
     */
    public List<CharacterResponse> getAllCharacters(Integer offset, Integer limit) {
        try {
            log.info("Fetching characters with offset: {}, limit: {}", offset, limit);
            
            CharacterDataWrapper response = marvelApiClient.getCharacters(offset, limit);
            
            if (response != null && response.getData() != null && response.getData().getResults() != null) {
                List<Character> characters = response.getData().getResults();
                log.info("Successfully fetched {} characters", characters.size());
                
                return characters.stream()
                        .map(CharacterResponse::fromCharacter)
                        .collect(Collectors.toList());
            } else {
                log.warn("No characters found in API response");
                return List.of();
            }
            
        } catch (Exception e) {
            log.error("Error fetching characters: {}", e.getMessage(), e);
            throw new RuntimeException("Error fetching characters from Marvel API", e);
        }
    }

    /**
     * Get character by ID
     */
    public CharacterResponse getCharacterById(Long characterId) {
        try {
            log.info("Fetching character with ID: {}", characterId);
            
            CharacterDataWrapper response = marvelApiClient.getCharacterById(characterId);
            
            if (response != null && response.getData() != null && 
                response.getData().getResults() != null && !response.getData().getResults().isEmpty()) {
                
                Character character = response.getData().getResults().get(0);
                log.info("Successfully fetched character: {}", character.getName());
                
                return CharacterResponse.fromCharacter(character);
            } else {
                log.warn("Character with ID {} not found", characterId);
                throw new RuntimeException("Character not found with ID: " + characterId);
            }
            
        } catch (Exception e) {
            log.error("Error fetching character with ID {}: {}", characterId, e.getMessage(), e);
            throw new RuntimeException("Error fetching character from Marvel API", e);
        }
    }

    /**
     * Search characters by name
     */
    public List<CharacterResponse> searchCharactersByName(String name, Integer offset, Integer limit) {
        try {
            log.info("Searching characters with name: {}, offset: {}, limit: {}", name, offset, limit);
            
            CharacterDataWrapper response = marvelApiClient.getCharactersByName(name, offset, limit);
            
            if (response != null && response.getData() != null && response.getData().getResults() != null) {
                List<Character> characters = response.getData().getResults();
                log.info("Successfully found {} characters matching name: {}", characters.size(), name);
                
                return characters.stream()
                        .map(CharacterResponse::fromCharacter)
                        .collect(Collectors.toList());
            } else {
                log.warn("No characters found matching name: {}", name);
                return List.of();
            }
            
        } catch (Exception e) {
            log.error("Error searching characters by name {}: {}", name, e.getMessage(), e);
            throw new RuntimeException("Error searching characters from Marvel API", e);
        }
    }
}