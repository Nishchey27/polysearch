package com.polysearch.backend.controller;

import com.polysearch.backend.dto.SearchResult;
import com.polysearch.backend.service.SearchService; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestParam; 

import java.util.List; 

@Controller
public class PageController {

    //Inject SearchService 
    private final SearchService searchService;

    public PageController(SearchService searchService) {
        this.searchService = searchService;
    }


    @GetMapping("/")
    public String home() {
        return "index";
    }

    //THIS IS OUR HTMX ENDPOINT
    @PostMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        
        System.out.println("HTMX Search Query: " + query);
        
        //Call our existing service 
        List<SearchResult> results = searchService.search(query);
        
        //Add the results to a "Model" object
        model.addAttribute("results", results);
        
        //Return the NAME of the HTML fragment file ("results.html")
        return "results";
    }
}