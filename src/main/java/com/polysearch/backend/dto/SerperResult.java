package com.polysearch.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SerperResult {
    private String title;
    private String link;
    private String snippet;
}
