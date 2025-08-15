package com.alkemy.marvel.service;

import com.alkemy.marvel.dto.ApiCallLogResponse;
import com.alkemy.marvel.entity.ApiCallLog;
import com.alkemy.marvel.repository.ApiCallLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for logging and retrieving API calls
 */
@Slf4j
@Service
public class ApiCallLogService {

    @Autowired
    private ApiCallLogRepository apiCallLogRepository;

    /**
     * Log an API call
     */
    public void logApiCall(HttpServletRequest request, Integer responseStatus) {
        try {
            String username = getCurrentUsername();
            String endpoint = request.getRequestURI();
            String method = request.getMethod();
            String params = request.getQueryString();
            String userAgent = request.getHeader("User-Agent");
            String ipAddress = getClientIpAddress(request);

            ApiCallLog apiCallLog = ApiCallLog.builder()
                    .endpoint(endpoint)
                    .requestMethod(method)
                    .requestParams(params)
                    .responseStatus(responseStatus)
                    .userAgent(userAgent)
                    .ipAddress(ipAddress)
                    .username(username)
                    .build();

            apiCallLogRepository.save(apiCallLog);
            
            log.debug("API call logged: {} {} by user {}", method, endpoint, username);
            
        } catch (Exception e) {
            log.error("Error logging API call: {}", e.getMessage(), e);
        }
    }

    /**
     * Get all API call logs with pagination
     */
    public List<ApiCallLogResponse> getAllApiCallLogs(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("calledAt").descending());
            Page<ApiCallLog> apiCallLogs = apiCallLogRepository.findAll(pageable);
            
            return apiCallLogs.getContent().stream()
                    .map(ApiCallLogResponse::fromEntity)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Error fetching API call logs: {}", e.getMessage(), e);
            throw new RuntimeException("Error fetching API call logs", e);
        }
    }

    /**
     * Get API call logs by username with pagination
     */
    public List<ApiCallLogResponse> getApiCallLogsByUsername(String username, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("calledAt").descending());
            Page<ApiCallLog> apiCallLogs = apiCallLogRepository.findByUsername(username, pageable);
            
            return apiCallLogs.getContent().stream()
                    .map(ApiCallLogResponse::fromEntity)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Error fetching API call logs for user {}: {}", username, e.getMessage(), e);
            throw new RuntimeException("Error fetching API call logs for user", e);
        }
    }

    /**
     * Get API call logs by endpoint with pagination
     */
    public List<ApiCallLogResponse> getApiCallLogsByEndpoint(String endpoint, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("calledAt").descending());
            Page<ApiCallLog> apiCallLogs = apiCallLogRepository.findByEndpointContaining(endpoint, pageable);
            
            return apiCallLogs.getContent().stream()
                    .map(ApiCallLogResponse::fromEntity)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Error fetching API call logs for endpoint {}: {}", endpoint, e.getMessage(), e);
            throw new RuntimeException("Error fetching API call logs for endpoint", e);
        }
    }

    /**
     * Get current authenticated username
     */
    private String getCurrentUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "anonymous";
        }
    }

    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader != null && !xForwardedForHeader.isEmpty()) {
            return xForwardedForHeader.split(",")[0].trim();
        }
        
        String xRealIpHeader = request.getHeader("X-Real-IP");
        if (xRealIpHeader != null && !xRealIpHeader.isEmpty()) {
            return xRealIpHeader;
        }
        
        return request.getRemoteAddr();
    }
}