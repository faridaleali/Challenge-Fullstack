package com.alkemy.marvel.service;

import com.alkemy.marvel.dto.CharacterResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Mock service for Marvel characters (temporary while API credentials are resolved)
 */
@Slf4j
@Service
@Primary // Using mock service because Marvel API credentials are invalid
public class MockCharacterService extends CharacterService {
    
    private static final List<CharacterResponse> MOCK_CHARACTERS = new ArrayList<>();
    
    static {
        // Initialize with some mock Marvel characters
        MOCK_CHARACTERS.add(CharacterResponse.builder()
                .id(1009610L)
                .name("Spider-Man")
                .description("Bitten by a radioactive spider, high school student Peter Parker gained the speed, strength and powers of a spider.")
                .thumbnailUrl("https://i.annihil.us/u/prod/marvel/i/mg/3/50/526548a343e4b.jpg")
                .comics(Arrays.asList("Amazing Spider-Man", "Spectacular Spider-Man", "Ultimate Spider-Man"))
                .series(Arrays.asList("Spider-Man Series", "Avengers"))
                .stories(Arrays.asList("Origin Story", "First Appearance"))
                .events(Arrays.asList("Civil War", "Secret Wars"))
                .build());
                
        MOCK_CHARACTERS.add(CharacterResponse.builder()
                .id(1009368L)
                .name("Iron Man")
                .description("Wounded, captured and forced to build a weapon by his enemies, billionaire industrialist Tony Stark instead created an advanced suit of armor.")
                .thumbnailUrl("https://i.annihil.us/u/prod/marvel/i/mg/9/c0/527bb7b37ff55.jpg")
                .comics(Arrays.asList("Iron Man", "Avengers", "Tales of Suspense"))
                .series(Arrays.asList("Iron Man Series", "Avengers"))
                .stories(Arrays.asList("Demon in a Bottle", "Armor Wars"))
                .events(Arrays.asList("Civil War", "Infinity War"))
                .build());
                
        MOCK_CHARACTERS.add(CharacterResponse.builder()
                .id(1009220L)
                .name("Captain America")
                .description("Vowing to serve his country any way he could, young Steve Rogers took the super soldier serum to become America's one-man army.")
                .thumbnailUrl("https://i.annihil.us/u/prod/marvel/i/mg/3/50/537ba56d31087.jpg")
                .comics(Arrays.asList("Captain America", "Avengers", "Captain America: The Winter Soldier"))
                .series(Arrays.asList("Captain America Series", "Avengers"))
                .stories(Arrays.asList("The First Avenger", "Winter Soldier"))
                .events(Arrays.asList("Civil War", "Secret Empire"))
                .build());
                
        MOCK_CHARACTERS.add(CharacterResponse.builder()
                .id(1009664L)
                .name("Thor")
                .description("As the Norse God of thunder and lightning, Thor wields one of the greatest weapons ever made, the enchanted hammer Mjolnir.")
                .thumbnailUrl("https://i.annihil.us/u/prod/marvel/i/mg/d/d0/5269657a74350.jpg")
                .comics(Arrays.asList("Thor", "Journey into Mystery", "The Mighty Thor"))
                .series(Arrays.asList("Thor Series", "Avengers"))
                .stories(Arrays.asList("God of Thunder", "Ragnarok"))
                .events(Arrays.asList("Fear Itself", "War of the Realms"))
                .build());
                
        MOCK_CHARACTERS.add(CharacterResponse.builder()
                .id(1009351L)
                .name("Hulk")
                .description("Caught in a gamma bomb explosion while trying to save the life of a teenager, Dr. Bruce Banner was transformed into the incredibly powerful creature called the Hulk.")
                .thumbnailUrl("https://i.annihil.us/u/prod/marvel/i/mg/5/a0/538615ca33ab0.jpg")
                .comics(Arrays.asList("Incredible Hulk", "Avengers", "Hulk"))
                .series(Arrays.asList("Hulk Series", "Avengers"))
                .stories(Arrays.asList("Planet Hulk", "World War Hulk"))
                .events(Arrays.asList("World War Hulk", "Secret Wars"))
                .build());
                
        MOCK_CHARACTERS.add(CharacterResponse.builder()
                .id(1009189L)
                .name("Black Widow")
                .description("Natasha Romanoff, former Russian spy, now Avenger.")
                .thumbnailUrl("https://i.annihil.us/u/prod/marvel/i/mg/f/30/50fecad1f395b.jpg")
                .comics(Arrays.asList("Black Widow", "Avengers", "Tales of Suspense"))
                .series(Arrays.asList("Black Widow Series", "Avengers"))
                .stories(Arrays.asList("Red Room", "Budapest Operation"))
                .events(Arrays.asList("Secret Wars", "Infinity War"))
                .build());
    }
    
    @Override
    public List<CharacterResponse> getAllCharacters(Integer offset, Integer limit) {
        log.info("Using MOCK data - Fetching characters with offset: {}, limit: {}", offset, limit);
        
        int start = offset != null ? offset : 0;
        int end = Math.min(start + (limit != null ? limit : 20), MOCK_CHARACTERS.size());
        
        if (start >= MOCK_CHARACTERS.size()) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(MOCK_CHARACTERS.subList(start, end));
    }
    
    @Override
    public CharacterResponse getCharacterById(Long characterId) {
        log.info("Using MOCK data - Fetching character with ID: {}", characterId);
        
        return MOCK_CHARACTERS.stream()
                .filter(c -> c.getId().equals(characterId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Character not found with ID: " + characterId));
    }
    
    @Override
    public List<CharacterResponse> searchCharactersByName(String name, Integer offset, Integer limit) {
        log.info("Using MOCK data - Searching characters by name: {}", name);
        
        List<CharacterResponse> filtered = MOCK_CHARACTERS.stream()
                .filter(c -> c.getName().toLowerCase().startsWith(name.toLowerCase()))
                .collect(java.util.stream.Collectors.toList());
        
        int start = offset != null ? offset : 0;
        int end = Math.min(start + (limit != null ? limit : 20), filtered.size());
        
        if (start >= filtered.size()) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(filtered.subList(start, end));
    }
}