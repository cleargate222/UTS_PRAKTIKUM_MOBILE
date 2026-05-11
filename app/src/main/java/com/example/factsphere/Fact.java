package com.example.factsphere;

public class Fact {
    private String title;
    private String shortFact;
    private String category;
    private String imageUrl;
    private String details;

    public Fact(String title, String shortFact, String category, String imageUrl, String details) {
        this.title = title;
        this.shortFact = shortFact;
        this.category = category;
        this.imageUrl = imageUrl;
        this.details = details;
    }

    public String getTitle() {
        return title;
    }

    public String getShortFact() {
        return shortFact;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDetails() {
        return details;
    }
}
