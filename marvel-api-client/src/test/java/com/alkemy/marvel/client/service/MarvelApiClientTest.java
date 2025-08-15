package com.alkemy.marvel.client.service;

import com.alkemy.marvel.client.config.MarvelApiConfig;
import com.alkemy.marvel.client.dto.Character;
import com.alkemy.marvel.client.dto.CharacterDataContainer;
import com.alkemy.marvel.client.dto.CharacterDataWrapper;
import com.alkemy.marvel.client.exception.MarvelApiException;
import com.alkemy.marvel.client.service.impl.MarvelApiClientImpl;
import com.alkemy.marvel.client.utils.MarvelAuthUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarvelApiClientTest {
    
    private MockWebServer mockWebServer;
    private MarvelApiClient marvelApiClient;
    private ObjectMapper objectMapper;
    
    @Mock
    private MarvelApiConfig config;
    
    @Mock
    private MarvelAuthUtils authUtils;
    
    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        
        objectMapper = new ObjectMapper();
        
        when(config.getBaseUrl()).thenReturn(mockWebServer.url("/").toString());
        when(config.getPublicKey()).thenReturn("testPublicKey");
        when(config.getPrivateKey()).thenReturn("testPrivateKey");
        when(config.getTimeout()).thenReturn(5000);
        when(config.getMaxRetries()).thenReturn(3);
        
        when(authUtils.generateTimestamp()).thenReturn("1234567890");
        when(authUtils.generateHash("1234567890", "testPrivateKey", "testPublicKey"))
                .thenReturn("testHash");
        
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
        
        marvelApiClient = new MarvelApiClientImpl(config, authUtils, webClient);
    }
    
    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
    
    @Test
    void testGetCharacters_Success() throws Exception {
        CharacterDataWrapper expectedResponse = createMockCharacterDataWrapper();
        String jsonResponse = objectMapper.writeValueAsString(expectedResponse);
        
        mockWebServer.enqueue(new MockResponse()
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));
        
        CharacterDataWrapper result = marvelApiClient.getCharacters(20, 0);
        
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("Ok", result.getStatus());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getResults().size());
        assertEquals("Spider-Man", result.getData().getResults().get(0).getName());
    }
    
    @Test
    void testGetCharacterById_Success() throws Exception {
        CharacterDataWrapper expectedResponse = createMockCharacterDataWrapper();
        String jsonResponse = objectMapper.writeValueAsString(expectedResponse);
        
        mockWebServer.enqueue(new MockResponse()
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));
        
        CharacterDataWrapper result = marvelApiClient.getCharacterById(1009610L);
        
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("Ok", result.getStatus());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getResults().size());
        assertEquals("Spider-Man", result.getData().getResults().get(0).getName());
    }
    
    @Test
    void testGetCharacters_ServerError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error"));
        
        assertThrows(MarvelApiException.class, () -> {
            marvelApiClient.getCharacters(20, 0);
        });
    }
    
    @Test
    void testGetCharacterById_NotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Character not found"));
        
        assertThrows(MarvelApiException.class, () -> {
            marvelApiClient.getCharacterById(999999L);
        });
    }
    
    private CharacterDataWrapper createMockCharacterDataWrapper() {
        CharacterDataWrapper wrapper = new CharacterDataWrapper();
        wrapper.setCode(200);
        wrapper.setStatus("Ok");
        wrapper.setCopyright("© 2023 MARVEL");
        wrapper.setAttributionText("Data provided by Marvel. © 2023 MARVEL");
        
        CharacterDataContainer container = new CharacterDataContainer();
        container.setOffset(0);
        container.setLimit(20);
        container.setTotal(1);
        container.setCount(1);
        
        Character character = new Character();
        character.setId(1009610L);
        character.setName("Spider-Man");
        character.setDescription("Bitten by a radioactive spider...");
        
        container.setResults(Collections.singletonList(character));
        wrapper.setData(container);
        
        return wrapper;
    }
}