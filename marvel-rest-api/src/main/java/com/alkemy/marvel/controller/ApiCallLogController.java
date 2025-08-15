package com.alkemy.marvel.controller;

import com.alkemy.marvel.dto.ApiCallLogResponse;
import com.alkemy.marvel.service.ApiCallLogService;
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
import java.util.List;

/**
 * API Call Log controller for handling API call logs (bitácora)
 */
@Slf4j
@RestController
@RequestMapping("/api/logs")
@Validated
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "API Logs", description = "API call logs endpoints for tracking API usage")
public class ApiCallLogController {

    @Autowired
    private ApiCallLogService apiCallLogService;

    @Operation(
        summary = "Get all API call logs",
        description = "Retrieve all API call logs with pagination"
    )
    @ApiResponse(responseCode = "200", description = "API logs retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping
    public ResponseEntity<?> getAllApiCallLogs(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            
            @Parameter(description = "Number of results per page")
            @RequestParam(defaultValue = "20") @Min(1) Integer size,
            
            HttpServletRequest request) {
        
        try {
            log.info("Fetching all API call logs - page: {}, size: {}", page, size);
            
            // Limit maximum page size
            if (size > 100) {
                size = 100;
            }
            
            List<ApiCallLogResponse> apiLogs = apiCallLogService.getAllApiCallLogs(page, size);
            
            // Log this API call (without causing infinite recursion)
            apiCallLogService.logApiCall(request, HttpStatus.OK.value());
            
            return ResponseEntity.ok(new ApiLogsResponse(apiLogs, page, size, apiLogs.size()));
            
        } catch (RuntimeException e) {
            log.error("Error fetching API call logs: {}", e.getMessage(), e);
            
            apiCallLogService.logApiCall(request, HttpStatus.INTERNAL_SERVER_ERROR.value());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error fetching API call logs", e.getMessage()));
        }
    }

    @Operation(
        summary = "Get API call logs by username",
        description = "Retrieve API call logs for a specific user with pagination"
    )
    @ApiResponse(responseCode = "200", description = "API logs retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getApiCallLogsByUsername(
            @Parameter(description = "Username to filter logs", required = true)
            @PathVariable String username,
            
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            
            @Parameter(description = "Number of results per page")
            @RequestParam(defaultValue = "20") @Min(1) Integer size,
            
            HttpServletRequest request) {
        
        try {
            log.info("Fetching API call logs for user: {} - page: {}, size: {}", username, page, size);
            
            // Limit maximum page size
            if (size > 100) {
                size = 100;
            }
            
            List<ApiCallLogResponse> apiLogs = apiCallLogService.getApiCallLogsByUsername(username, page, size);
            
            apiCallLogService.logApiCall(request, HttpStatus.OK.value());
            
            return ResponseEntity.ok(new ApiLogsResponse(apiLogs, page, size, apiLogs.size()));
            
        } catch (RuntimeException e) {
            log.error("Error fetching API call logs for user {}: {}", username, e.getMessage(), e);
            
            apiCallLogService.logApiCall(request, HttpStatus.INTERNAL_SERVER_ERROR.value());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error fetching API call logs for user", e.getMessage()));
        }
    }

    @Operation(
        summary = "Get API call logs by endpoint",
        description = "Retrieve API call logs for a specific endpoint with pagination"
    )
    @ApiResponse(responseCode = "200", description = "API logs retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/endpoint")
    public ResponseEntity<?> getApiCallLogsByEndpoint(
            @Parameter(description = "Endpoint path to filter logs", required = true)
            @RequestParam String endpoint,
            
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            
            @Parameter(description = "Number of results per page")
            @RequestParam(defaultValue = "20") @Min(1) Integer size,
            
            HttpServletRequest request) {
        
        try {
            log.info("Fetching API call logs for endpoint: {} - page: {}, size: {}", endpoint, page, size);
            
            // Limit maximum page size
            if (size > 100) {
                size = 100;
            }
            
            List<ApiCallLogResponse> apiLogs = apiCallLogService.getApiCallLogsByEndpoint(endpoint, page, size);
            
            apiCallLogService.logApiCall(request, HttpStatus.OK.value());
            
            return ResponseEntity.ok(new ApiLogsResponse(apiLogs, page, size, apiLogs.size()));
            
        } catch (RuntimeException e) {
            log.error("Error fetching API call logs for endpoint {}: {}", endpoint, e.getMessage(), e);
            
            apiCallLogService.logApiCall(request, HttpStatus.INTERNAL_SERVER_ERROR.value());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error fetching API call logs for endpoint", e.getMessage()));
        }
    }

    /**
     * API logs response wrapper with pagination info
     */
    public static class ApiLogsResponse {
        private List<ApiCallLogResponse> data;
        private Integer page;
        private Integer size;
        private Integer count;

        public ApiLogsResponse(List<ApiCallLogResponse> data, Integer page, Integer size, Integer count) {
            this.data = data;
            this.page = page;
            this.size = size;
            this.count = count;
        }

        public List<ApiCallLogResponse> getData() {
            return data;
        }

        public Integer getPage() {
            return page;
        }

        public Integer getSize() {
            return size;
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