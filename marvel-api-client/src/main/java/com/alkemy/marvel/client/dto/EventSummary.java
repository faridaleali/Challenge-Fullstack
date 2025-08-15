package com.alkemy.marvel.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventSummary {
    private String resourceURI;
    private String name;
}