package com.alkemy.marvel.dto;

import com.alkemy.marvel.entity.ApiCallLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * API Call Log response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiCallLogResponse {
    
    private Long id;
    private String endpoint;
    private String requestMethod;
    private String requestParams;
    private Integer responseStatus;
    private String userAgent;
    private String ipAddress;
    private String username;
    private LocalDateTime calledAt;
    
    /**
     * Convert ApiCallLog entity to ApiCallLogResponse
     */
    public static ApiCallLogResponse fromEntity(ApiCallLog apiCallLog) {
        return ApiCallLogResponse.builder()
                .id(apiCallLog.getId())
                .endpoint(apiCallLog.getEndpoint())
                .requestMethod(apiCallLog.getRequestMethod())
                .requestParams(apiCallLog.getRequestParams())
                .responseStatus(apiCallLog.getResponseStatus())
                .userAgent(apiCallLog.getUserAgent())
                .ipAddress(apiCallLog.getIpAddress())
                .username(apiCallLog.getUsername())
                .calledAt(apiCallLog.getCalledAt())
                .build();
    }
}