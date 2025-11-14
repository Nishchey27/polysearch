package com.polysearch.backend.service;

import com.polysearch.backend.dto.LingoDetectRequest;
import com.polysearch.backend.dto.LingoDetectResponse;
import com.polysearch.backend.dto.LingoTranslateRequest;
import com.polysearch.backend.dto.LingoTranslateResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TranslationService {

    private RestClient restClient;

    // We don't need @Value for the URL anymore,
    // since it's always our local proxy.

    public TranslationService() {
    }

    @PostConstruct
    private void init() {
        // 1. THE BIG CHANGE:
        //    We are now calling our OWN proxy server.
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:3001") // <-- CALLING THE PROXY
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
        // We don't need the Lingo API key here anymore,
        // because the Node.js proxy is handling it.
    }

    // THIS IS THE REAL IMPLEMENTATION
    public String detectLanguage(String text) {
        System.out.println("[Java] Calling Proxy to detect: " + text);
        LingoDetectRequest requestBody = new LingoDetectRequest(text);

        try {
            LingoDetectResponse response = restClient.post()
                    .uri("/detect") // <-- 2. CALLING THE PROXY'S /detect ENDPOINT
                    .body(requestBody)
                    .retrieve()
                    .body(LingoDetectResponse.class);

            if (response != null && response.locale() != null) {
                return response.locale();
            }
        } catch (Exception e) {
            System.err.println("Proxy detection failed: " + e.getMessage());
        }
        return "en"; // Fallback
    }

    // THIS IS THE REAL IMPLEMENTATION
    public String translate(String text, String sourceLang, String targetLang) {
        System.out.println("[Java] Calling Proxy to translate ("+sourceLang+"->"+targetLang+"): " + text);
        LingoTranslateRequest requestBody = new LingoTranslateRequest(text, sourceLang, targetLang);

        try {
            LingoTranslateResponse response = restClient.post()
                    .uri("/translate") // <-- 3. CALLING THE PROXY'S /translate ENDPOINT
                    .body(requestBody)
                    .retrieve()
                    .body(LingoTranslateResponse.class);

            if (response != null && response.text() != null) {
                return response.text();
            }
        } catch (Exception e) {
            System.err.println("Proxy translation failed: " + e.getMessage());
        }
        return text; // Fallback
    }

    
    public java.util.Map<String, String> translateBatch(java.util.Map<String, String> content, String sourceLang, String targetLang) {
        System.out.println("[Java] Calling Proxy for BATCH translation of " + content.size() + " items.");
        
        
        record BatchRequest(java.util.Map<String, String> content, String sourceLocale, String targetLocale) {}
        record BatchResponse(java.util.Map<String, String> content) {}

        BatchRequest requestBody = new BatchRequest(content, sourceLang, targetLang);

        try {
            BatchResponse response = restClient.post()
                    .uri("/translate-batch") // Call the new endpoint
                    .body(requestBody)
                    .retrieve()
                    .body(BatchResponse.class);

            if (response != null && response.content() != null) {
                return response.content();
            }
        } catch (Exception e) {
            System.err.println("Proxy batch translation failed: " + e.getMessage());
        }
        return content; 
    }
}