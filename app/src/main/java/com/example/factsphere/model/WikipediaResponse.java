package com.example.factsphere.model;

import com.google.gson.annotations.SerializedName;

public class WikipediaResponse {

    @SerializedName("title")
    private String title;

    @SerializedName("extract")
    private String extract;

    @SerializedName("thumbnail")
    private Thumbnail thumbnail;

    @SerializedName("content_urls")
    private ContentUrls contentUrls;

    public String getTitle()             { return title; }
    public String getExtract()           { return extract; }
    public Thumbnail getThumbnail()      { return thumbnail; }
    public ContentUrls getContentUrls()  { return contentUrls; }

    public static class Thumbnail {
        @SerializedName("source")
        private String source;
        public String getSource() { return source; }
    }

    public static class ContentUrls {
        @SerializedName("desktop")
        private Desktop desktop;
        public Desktop getDesktop() { return desktop; }

        public static class Desktop {
            @SerializedName("page")
            private String page;
            public String getPage() { return page; }
        }
    }
}