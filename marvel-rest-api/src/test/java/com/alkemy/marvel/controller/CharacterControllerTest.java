package com.alkemy.marvel.controller;

import com.alkemy.marvel.dto.CharacterResponse;
import com.alkemy.marvel.service.ApiCallLogService;
import com.alkemy.marvel.service.CharacterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for CharacterController
 */
@ExtendWith(MockitoExtension.class)
@WebMvcTest(CharacterController.class)
class CharacterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CharacterService characterService;

    @MockBean
    private ApiCallLogService apiCallLogService;

    @Autowired
    private ObjectMapper objectMapper;

    private CharacterResponse mockCharacter;
    private List<CharacterResponse> mockCharacters;

    @BeforeEach
    void setUp() {
        mockCharacter = CharacterResponse.builder()
                .id(1011334)
                .name("3-D Man")
                .description("Test description")
                .thumbnailUrl("http://i.annihil.us/u/prod/marvel/i/mg/c/e0/535fecbbb9784.jpg")
                .comics(Arrays.asList("Avengers: The Initiative #14", "Avengers: The Initiative #15"))
                .series(Arrays.asList("Avengers: The Initiative (2007 - 2010)"))
                .stories(Arrays.asList("Cover #19947", "Interior #19948"))
                .events(Arrays.asList("Secret Invasion"))
                .urls(Arrays.asList("detail: http://marvel.com/characters/74/3-d_man"))
                .build();

        mockCharacters = Arrays.asList(mockCharacter);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllCharacters_Success() throws Exception {
        // Given
        when(characterService.getAllCharacters(anyInt(), anyInt())).thenReturn(mockCharacters);
        doNothing().when(apiCallLogService).logApiCall(any(), anyInt());

        // When & Then
        mockMvc.perform(get("/characters")
                .with(csrf())
                .param("offset", "0")
                .param("limit", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1011334))
                .andExpect(jsonPath("$.data[0].name").value("3-D Man"))
                .andExpect(jsonPath("$.offset").value(0))
                .andExpect(jsonPath("$.limit").value(20))
                .andExpect(jsonPath("$.count").value(1));

        verify(characterService).getAllCharacters(0, 20);
        verify(apiCallLogService).logApiCall(any(), eq(200));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllCharacters_WithNameFilter_Success() throws Exception {
        // Given
        String searchName = "Spider";
        when(characterService.searchCharactersByName(eq(searchName), anyInt(), anyInt())).thenReturn(mockCharacters);
        doNothing().when(apiCallLogService).logApiCall(any(), anyInt());

        // When & Then
        mockMvc.perform(get("/characters")
                .with(csrf())
                .param("offset", "0")
                .param("limit", "20")
                .param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1011334))
                .andExpect(jsonPath("$.data[0].name").value("3-D Man"));

        verify(characterService).searchCharactersByName(searchName, 0, 20);
        verify(characterService, never()).getAllCharacters(anyInt(), anyInt());
        verify(apiCallLogService).logApiCall(any(), eq(200));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllCharacters_ServiceException_ReturnsInternalServerError() throws Exception {
        // Given
        when(characterService.getAllCharacters(anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Marvel API error"));
        doNothing().when(apiCallLogService).logApiCall(any(), anyInt());

        // When & Then
        mockMvc.perform(get("/characters")
                .with(csrf())
                .param("offset", "0")
                .param("limit", "20"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Error fetching characters"))
                .andExpect(jsonPath("$.message").value("Marvel API error"));

        verify(characterService).getAllCharacters(0, 20);
        verify(apiCallLogService).logApiCall(any(), eq(500));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCharacterById_Success() throws Exception {
        // Given
        Integer characterId = 1011334;
        when(characterService.getCharacterById(characterId)).thenReturn(mockCharacter);
        doNothing().when(apiCallLogService).logApiCall(any(), anyInt());

        // When & Then
        mockMvc.perform(get("/characters/{id}", characterId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1011334))
                .andExpect(jsonPath("$.name").value("3-D Man"))
                .andExpect(jsonPath("$.description").value("Test description"));

        verify(characterService).getCharacterById(characterId);
        verify(apiCallLogService).logApiCall(any(), eq(200));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCharacterById_NotFound_ReturnsNotFound() throws Exception {
        // Given
        Integer characterId = 999999;
        when(characterService.getCharacterById(characterId))
                .thenThrow(new RuntimeException("Character not found with ID: " + characterId));
        doNothing().when(apiCallLogService).logApiCall(any(), anyInt());

        // When & Then
        mockMvc.perform(get("/characters/{id}", characterId)
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Error fetching character"))
                .andExpect(jsonPath("$.message").value("Character not found with ID: " + characterId));

        verify(characterService).getCharacterById(characterId);
        verify(apiCallLogService).logApiCall(any(), eq(404));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCharacterById_ServiceException_ReturnsInternalServerError() throws Exception {
        // Given
        Integer characterId = 1011334;
        when(characterService.getCharacterById(characterId))
                .thenThrow(new RuntimeException("Marvel API connection error"));
        doNothing().when(apiCallLogService).logApiCall(any(), anyInt());

        // When & Then
        mockMvc.perform(get("/characters/{id}", characterId)
                .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Error fetching character"))
                .andExpect(jsonPath("$.message").value("Marvel API connection error"));

        verify(characterService).getCharacterById(characterId);
        verify(apiCallLogService).logApiCall(any(), eq(500));
    }

    @Test
    void getAllCharacters_Unauthorized_ReturnsUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/characters"))
                .andExpect(status().isUnauthorized());

        verify(characterService, never()).getAllCharacters(anyInt(), anyInt());
        verify(apiCallLogService, never()).logApiCall(any(), anyInt());
    }

    @Test
    void getCharacterById_Unauthorized_ReturnsUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/characters/1011334"))
                .andExpect(status().isUnauthorized());

        verify(characterService, never()).getCharacterById(anyInt());
        verify(apiCallLogService, never()).logApiCall(any(), anyInt());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllCharacters_LimitExceeds100_CapsTo100() throws Exception {
        // Given
        when(characterService.getAllCharacters(anyInt(), eq(100))).thenReturn(mockCharacters);
        doNothing().when(apiCallLogService).logApiCall(any(), anyInt());

        // When & Then
        mockMvc.perform(get("/characters")
                .with(csrf())
                .param("offset", "0")
                .param("limit", "150")) // Request more than 100
                .andExpect(status().isOk());

        verify(characterService).getAllCharacters(0, 100); // Should be capped to 100
    }
}