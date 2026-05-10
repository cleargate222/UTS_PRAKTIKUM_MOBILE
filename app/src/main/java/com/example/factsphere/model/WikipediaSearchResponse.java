package com.example.factsphere.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WikipediaSearchResponse {

    @SerializedName("query")
    private Query query;

    public Query getQuery() { return query; }

    public static class Query {
        @SerializedName("search")
        private List<SearchResult> search;
        public List<SearchResult> getSearch() { return search; }
    }

    public static class SearchResult {
        @SerializedName("title")
        private String title;

        @SerializedName("snippet")
        private String snippet;

        @SerializedName("pageid")
        private int pageId;

        public String getTitle()   { return title; }
        public String getSnippet() { return snippet; }
        public int getPageId()     { return pageId; }
    }
}