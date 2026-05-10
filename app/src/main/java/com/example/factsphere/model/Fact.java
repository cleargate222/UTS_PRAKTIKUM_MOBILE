package com.example.factsphere.model;

public class Fact {
    private String id;
    private String title;
    private String shortFact;
    private String longArticle;
    private String category;
    private String imageUrl;

    public Fact(String id, String title, String shortFact,
                String longArticle, String category, String imageUrl) {
        this.id = id;
        this.title = title;
        this.shortFact = shortFact;
        this.longArticle = longArticle;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public String getId()          { return id; }
    public String getTitle()       { return title; }
    public String getShortFact()   { return shortFact; }
    public String getLongArticle() { return longArticle; }
    public String getCategory()    { return category; }
    public String getImageUrl()    { return imageUrl; }
}