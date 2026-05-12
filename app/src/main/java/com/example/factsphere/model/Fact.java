package com.example.factsphere.model;

public class Fact {
<<<<<<< HEAD
=======

>>>>>>> origin/feature/fact-model
    private String id;
    private String title;
    private String shortFact;
    private String longArticle;
    private String category;
    private String imageUrl;
<<<<<<< HEAD

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
=======
    private String wikipediaTitle;
    private boolean isFavorite;

    public Fact() {}

    public Fact(String id, String title, String shortFact,
                String category, String imageUrl, String wikipediaTitle) {
        this.id = id;
        this.title = title;
        this.shortFact = shortFact;
        this.category = category;
        this.imageUrl = imageUrl;
        this.wikipediaTitle = wikipediaTitle;
    }

    // Getters
    public String getId()             { return id; }
    public String getTitle()          { return title; }
    public String getShortFact()      { return shortFact; }
    public String getLongArticle()    { return longArticle; }
    public String getCategory()       { return category; }
    public String getImageUrl()       { return imageUrl; }
    public String getWikipediaTitle() { return wikipediaTitle; }
    public boolean isFavorite()       { return isFavorite; }

    // Setters
    public void setId(String id)                   { this.id = id; }
    public void setTitle(String title)             { this.title = title; }
    public void setShortFact(String shortFact)     { this.shortFact = shortFact; }
    public void setLongArticle(String longArticle) { this.longArticle = longArticle; }
    public void setCategory(String category)       { this.category = category; }
    public void setImageUrl(String imageUrl)       { this.imageUrl = imageUrl; }
    public void setWikipediaTitle(String title)    { this.wikipediaTitle = title; }
    public void setFavorite(boolean favorite)      { this.isFavorite = favorite; }
>>>>>>> origin/feature/fact-model
}