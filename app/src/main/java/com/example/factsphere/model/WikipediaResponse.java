package com.example.factsphere.model;

import com.google.gson.annotations.SerializedName;

public class WikipediaResponse {

    @SerializedName("title")
    private String title;

    @SerializedName("extract")
    private String extract;

    @SerializedName("thumbnail")
    private Thumbnail thumbnail;

    public String getTitle()        { return title; }
    public String getExtract()      { return extract; }
    public Thumbnail getThumbnail() { return thumbnail; }

    public static class Thumbnail {
        @SerializedName("source")
        private String source;
        public String getSource() { return source; }
    }
}