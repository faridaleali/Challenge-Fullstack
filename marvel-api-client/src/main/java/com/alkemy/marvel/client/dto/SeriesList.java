package com.alkemy.marvel.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeriesList {
    private int available;
    private String collectionURI;
    private List<SeriesSummary> items;
    private int returned;
}