package com.polysearch.backend.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LingoDetectResponse(String locale) {
}
