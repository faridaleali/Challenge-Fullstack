package com.alkemy.marvel.repository;

import com.alkemy.marvel.entity.ApiCallLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApiCallLogRepository extends JpaRepository<ApiCallLog, Long> {
    
    List<ApiCallLog> findAllByOrderByCalledAtDesc();
    
    List<ApiCallLog> findByUsernameOrderByCalledAtDesc(String username);
    
    List<ApiCallLog> findByCalledAtBetweenOrderByCalledAtDesc(LocalDateTime start, LocalDateTime end);
    
    List<ApiCallLog> findByEndpointContainingOrderByCalledAtDesc(String endpoint);
    
    // Paginated methods
    Page<ApiCallLog> findByUsername(String username, Pageable pageable);
    
    Page<ApiCallLog> findByEndpointContaining(String endpoint, Pageable pageable);
}