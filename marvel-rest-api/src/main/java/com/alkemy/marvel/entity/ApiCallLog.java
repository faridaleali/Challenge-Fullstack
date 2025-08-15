package com.alkemy.marvel.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_call_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiCallLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String endpoint;
    
    @Column(name = "request_method", nullable = false)
    private String requestMethod;
    
    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams;
    
    @Column(name = "response_status")
    private Integer responseStatus;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "username")
    private String username;
    
    @Column(name = "called_at", nullable = false)
    private LocalDateTime calledAt;
    
    @PrePersist
    protected void onCreate() {
        calledAt = LocalDateTime.now();
    }
}