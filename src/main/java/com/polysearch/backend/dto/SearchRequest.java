package com.polysearch.backend.dto;

import lombok.Data;
@Data
public class SearchRequest {
    private String query;
//    private String query;: This means we expect to receive JSON that looks like this:
//    {"query": "some search term"}.

}
