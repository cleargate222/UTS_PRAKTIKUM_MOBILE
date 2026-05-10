package com.example.factsphere.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String WIKI_BASE_URL = "https://id.wikipedia.org/";

    private static Retrofit wikiInstance;

    public static Retrofit getWikiClient() {
        if (wikiInstance == null) {
            wikiInstance = new Retrofit.Builder()
                    .baseUrl(WIKI_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return wikiInstance;
    }

    public static WikipediaApiService getWikiService() {
        return getWikiClient().create(WikipediaApiService.class);
    }
}