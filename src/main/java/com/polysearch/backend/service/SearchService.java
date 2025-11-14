package com.polysearch.backend.service;

import com.polysearch.backend.dto.SearchResult;
import com.polysearch.backend.dto.SerperResponse;
import com.polysearch.backend.dto.SerperResult;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.HashMap;
import java.util.stream.IntStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private RestClient restClient;


    private final TranslationService translationService;

    @Value("${serper.api.url}")
    private String serperApiUrl;

    @Value("${serper.api.key}")
    private String serperApiKey;

    //  We inject TranslationService here
    public SearchService(TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostConstruct
    private void init() {
        this.restClient = RestClient.builder()
                .baseUrl(serperApiUrl)
                .defaultHeader("X-API-KEY", serperApiKey)
                .build();
    }

    public List<SearchResult> search(String query) {

        // 1. Detect and Translate Query (Fast enough to keep as-is)
        String sourceLang = translationService.detectLanguage(query);
        String englishQuery = query;
        if (!sourceLang.equals("en")) {
            englishQuery = translationService.translate(query, sourceLang, "en");
        }
        
        // 2. Call Serper
        Map<String, String> requestBody = Map.of("q", englishQuery);
        SerperResponse serperResponse = null;
        try {
            serperResponse = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(SerperResponse.class);
        } catch (Exception e) {
            return List.of();
        }

        // 3. BATCH TRANSLATION MAGIC
        if (serperResponse != null && serperResponse.getOrganic() != null) {
            List<SerperResult> rawResults = serperResponse.getOrganic();
            
            // A. Prepare the Batch Map
            // We will create keys like: "title_0", "snippet_0", "title_1", "snippet_1"...
            java.util.Map<String, String> batchMap = new java.util.HashMap<>();
            for (int i = 0; i < rawResults.size(); i++) {
                batchMap.put("title_" + i, rawResults.get(i).getTitle());
                batchMap.put("snippet_" + i, rawResults.get(i).getSnippet());
            }

            // B. Send the WHOLE map to be translated at once
            java.util.Map<String, String> translatedMap = translationService.translateBatch(batchMap, "en", sourceLang);

            // C. Re-assemble the results using the translated values
            return java.util.stream.IntStream.range(0, rawResults.size())
                    .mapToObj(i -> new SearchResult(
                            translatedMap.getOrDefault("title_" + i, rawResults.get(i).getTitle()),
                            translatedMap.getOrDefault("snippet_" + i, rawResults.get(i).getSnippet()),
                            rawResults.get(i).getLink()
                    ))
                    .collect(Collectors.toList());
        }

        return List.of();
    }
}