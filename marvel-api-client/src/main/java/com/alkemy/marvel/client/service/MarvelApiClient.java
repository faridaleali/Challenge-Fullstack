package com.alkemy.marvel.client.service;

import com.alkemy.marvel.client.dto.CharacterDataWrapper;

public interface MarvelApiClient {
    CharacterDataWrapper getCharacters(Integer offset, Integer limit);
    CharacterDataWrapper getCharacterById(Long characterId);
    CharacterDataWrapper getCharactersByName(String name, Integer offset, Integer limit);
}