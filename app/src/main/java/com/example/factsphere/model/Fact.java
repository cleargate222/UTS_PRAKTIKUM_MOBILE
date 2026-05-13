package com.example.factsphere.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Fact implements Parcelable {
    private String id;
    private String title;
    private String shortFact;
    private String longArticle;
    private String category;
    private String imageUrl;
    private String wikipediaTitle;
    private boolean isFavorite;

    public Fact() {}

    public Fact(String id, String title, String shortFact,
                String category, String imageUrl, String wikipediaTitle) {
        this.id             = id;
        this.title          = title;
        this.shortFact      = shortFact;
        this.category       = category;
        this.imageUrl       = imageUrl;
        this.wikipediaTitle = wikipediaTitle;
    }

    public String  getId()             { return id; }
    public String  getTitle()          { return title; }
    public String  getShortFact()      { return shortFact; }
    public String  getLongArticle()    { return longArticle; }
    public String  getCategory()       { return category; }
    public String  getImageUrl()       { return imageUrl; }
    public String  getWikipediaTitle() { return wikipediaTitle; }
    public boolean isFavorite()        { return isFavorite; }
    public void    setFavorite(boolean favorite) { isFavorite = favorite; }

    protected Fact(Parcel in) {
        id             = in.readString();
        title          = in.readString();
        shortFact      = in.readString();
        longArticle    = in.readString();
        category       = in.readString();
        imageUrl       = in.readString();
        wikipediaTitle = in.readString();
        isFavorite     = in.readByte() != 0;
    }

    public static final Creator<Fact> CREATOR = new Creator<Fact>() {
        @Override
        public Fact createFromParcel(Parcel in) { return new Fact(in); }
        @Override
        public Fact[] newArray(int size) { return new Fact[size]; }
    };

    @Override public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(shortFact);
        dest.writeString(longArticle);
        dest.writeString(category);
        dest.writeString(imageUrl);
        dest.writeString(wikipediaTitle);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }
}