package com.polysearch.backend.controller;

import com.polysearch.backend.dto.SearchRequest;
// We don't need SearchResult DTO for this step
import com.polysearch.backend.service.SearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    // 1. Ask Spring to "inject" the SearchService
    private final SearchService searchService;

    // 2. A constructor to receive the service from Spring
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping
    public String performSearch(@RequestBody SearchRequest searchRequest) {

        // 3. Log the query
        System.out.println("Received search query: " + searchRequest.getQuery());

        // 4. DELEGATE! Pass the query to the service and return its response
        return searchService.search(searchRequest.getQuery());
    }
}