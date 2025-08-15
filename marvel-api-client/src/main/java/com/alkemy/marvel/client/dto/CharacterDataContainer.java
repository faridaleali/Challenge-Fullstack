package com.alkemy.marvel.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterDataContainer {
    private int offset;
    private int limit;
    private int total;
    private int count;
    private List<Character> results;
}