package com.alkemy.marvel.dto;

import com.alkemy.marvel.client.dto.Character;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Character response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterResponse {
    
    private Long id;
    private String name;
    private String description;
    private String thumbnailUrl;
    private List<String> comics;
    private List<String> series;
    private List<String> stories;
    private List<String> events;
    private List<String> urls;
    
    /**
     * Convert Marvel API Character to CharacterResponse
     */
    public static CharacterResponse fromCharacter(Character character) {
        return CharacterResponse.builder()
                .id(character.getId())
                .name(character.getName())
                .description(character.getDescription())
                .thumbnailUrl(character.getThumbnail() != null ? 
                    character.getThumbnail().getPath() + "." + character.getThumbnail().getExtension() : null)
                .comics(character.getComics() != null && character.getComics().getItems() != null ?
                    character.getComics().getItems().stream()
                        .map(comic -> comic.getName())
                        .collect(java.util.stream.Collectors.toList()) : null)
                .series(character.getSeries() != null && character.getSeries().getItems() != null ?
                    character.getSeries().getItems().stream()
                        .map(serie -> serie.getName())
                        .collect(java.util.stream.Collectors.toList()) : null)
                .stories(character.getStories() != null && character.getStories().getItems() != null ?
                    character.getStories().getItems().stream()
                        .map(story -> story.getName())
                        .collect(java.util.stream.Collectors.toList()) : null)
                .events(character.getEvents() != null && character.getEvents().getItems() != null ?
                    character.getEvents().getItems().stream()
                        .map(event -> event.getName())
                        .collect(java.util.stream.Collectors.toList()) : null)
                .urls(character.getUrls() != null ?
                    java.util.Arrays.asList(character.getUrls().getType() + ": " + character.getUrls().getUrl()) : null)
                .build();
    }
}