package com.polysearch.backend.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class SearchService {

    private RestClient restClient;

    @Value("${serper.api.url}")
    private String serperApiUrl;

    @Value("${serper.api.key}")
    private String serperApiKey;

    public SearchService() {
    }

    @PostConstruct
    private void init() {
        this.restClient = RestClient.builder()
                .baseUrl(serperApiUrl)
                .defaultHeader("X-API-KEY", serperApiKey)
                .build();
    }

    public String search(String query) {

        System.out.println("Calling Serper API with query: " + query);

        Map<String, String> requestBody = Map.of("q", query);

        String response = restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        return response;
    }
}