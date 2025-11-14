package com.polysearch.backend.dto;

public record LingoTranslateRequest(String text, String sourceLocale, String targetLocale) {
}
