package com.alkemy.marvel.controller;

import com.alkemy.marvel.dto.CharacterResponse;
import com.alkemy.marvel.service.ApiCallLogService;
import com.alkemy.marvel.service.CharacterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * Character controller for handling Marvel character operations
 */
@Slf4j
@RestController
@RequestMapping("/api/characters")
@Validated
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Characters", description = "Marvel characters endpoints")
public class CharacterController {

    @Autowired
    private CharacterService characterService;

    @Autowired
    private ApiCallLogService apiCallLogService;

    @Operation(
        summary = "Get all characters",
        description = "Retrieve all Marvel characters with optional pagination and name filtering"
    )
    @ApiResponse(responseCode = "200", description = "Characters retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping
    public ResponseEntity<?> getAllCharacters(
            @Parameter(description = "Number of results to skip (pagination offset)")
            @RequestParam(defaultValue = "0") @Min(0) Integer offset,
            
            @Parameter(description = "Number of results to return (max 100)")
            @RequestParam(defaultValue = "20") @Min(1) Integer limit,
            
            @Parameter(description = "Filter characters by name (starts with)")
            @RequestParam(required = false) String name,
            
            HttpServletRequest request) {
        
        try {
            log.info("Fetching characters - offset: {}, limit: {}, name: {}", offset, limit, name);
            
            // Limit maximum results per request
            if (limit > 100) {
                limit = 100;
            }
            
            List<CharacterResponse> characters;
            
            if (name != null && !name.trim().isEmpty()) {
                characters = characterService.searchCharactersByName(name.trim(), offset, limit);
            } else {
                characters = characterService.getAllCharacters(offset, limit);
            }
            
            // Log the successful API call
            apiCallLogService.logApiCall(request, HttpStatus.OK.value());
            
            return ResponseEntity.ok(new CharactersResponse(characters, offset, limit, characters.size()));
            
        } catch (RuntimeException e) {
            log.error("Error fetching characters: {}", e.getMessage(), e);
            
            // Log the failed API call
            apiCallLogService.logApiCall(request, HttpStatus.INTERNAL_SERVER_ERROR.value());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error fetching characters", e.getMessage()));
        }
    }

    @Operation(
        summary = "Get character by ID",
        description = "Retrieve a specific Marvel character by their ID"
    )
    @ApiResponse(responseCode = "200", description = "Character retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Character not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCharacterById(
            @Parameter(description = "Character ID", required = true)
            @PathVariable @Positive Long id,
            HttpServletRequest request) {
        
        try {
            log.info("Fetching character with ID: {}", id);
            
            CharacterResponse character = characterService.getCharacterById(id);
            
            // Log the successful API call
            apiCallLogService.logApiCall(request, HttpStatus.OK.value());
            
            return ResponseEntity.ok(character);
            
        } catch (RuntimeException e) {
            log.error("Error fetching character with ID {}: {}", id, e.getMessage(), e);
            
            HttpStatus status = e.getMessage().contains("not found") ? 
                HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            
            // Log the failed API call
            apiCallLogService.logApiCall(request, status.value());
            
            return ResponseEntity.status(status)
                    .body(new ErrorResponse("Error fetching character", e.getMessage()));
        }
    }

    /**
     * Characters response wrapper with pagination info
     */
    public static class CharactersResponse {
        private List<CharacterResponse> data;
        private Integer offset;
        private Integer limit;
        private Integer count;

        public CharactersResponse(List<CharacterResponse> data, Integer offset, Integer limit, Integer count) {
            this.data = data;
            this.offset = offset;
            this.limit = limit;
            this.count = count;
        }

        public List<CharacterResponse> getData() {
            return data;
        }

        public Integer getOffset() {
            return offset;
        }

        public Integer getLimit() {
            return limit;
        }

        public Integer getCount() {
            return count;
        }
    }

    /**
     * Error response DTO for consistent error handling
     */
    public static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }
    }
}