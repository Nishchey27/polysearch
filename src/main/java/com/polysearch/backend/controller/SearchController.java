package com.polysearch.backend.controller;

import com.polysearch.backend.dto.SearchRequest;
import com.polysearch.backend.dto.SearchResult;
import com.polysearch.backend.service.SearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping

    public List<SearchResult> performSearch(@RequestBody SearchRequest searchRequest) {

        System.out.println("Received search query: " + searchRequest.getQuery());


        return searchService.search(searchRequest.getQuery());
    }
}