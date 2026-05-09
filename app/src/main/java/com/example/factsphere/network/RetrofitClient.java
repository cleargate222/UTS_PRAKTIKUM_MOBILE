package com.example.factsphere.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RetrofitClient {

    private static final String NINJA_BASE_URL = "https://api.api-ninjas.com/";
    private static final String WIKI_BASE_URL  = "https://en.wikipedia.org/";
    private static final String API_KEY = "YOUR_API_KEY";

    private static Retrofit ninjaInstance;
    private static Retrofit wikiInstance;

    public static Retrofit getNinjaClient() {
        if (ninjaInstance == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request request = chain.request().newBuilder()
                                .addHeader("X-Api-Key", API_KEY)
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

            ninjaInstance = new Retrofit.Builder()
                    .baseUrl(NINJA_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return ninjaInstance;
    }

    public static Retrofit getWikiClient() {
        if (wikiInstance == null) {
            wikiInstance = new Retrofit.Builder()
                    .baseUrl(WIKI_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return wikiInstance;
    }

    public static FactsApiService getFactsService() {
        return getNinjaClient().create(FactsApiService.class);
    }

    public static WikipediaApiService getWikiService() {
        return getWikiClient().create(WikipediaApiService.class);
    }
}