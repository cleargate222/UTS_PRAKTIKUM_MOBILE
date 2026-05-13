package com.example.factsphere.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String WIKI_BASE_URL = "https://id.wikipedia.org/";

    private static Retrofit wikiInstance;

    public static Retrofit getWikiClient() {
        if (wikiInstance == null) {

            // Tambahkan User-Agent (WAJIB untuk Wikipedia)
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("User-Agent", "FactSphere/1.0 (Android App)")
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

            wikiInstance = new Retrofit.Builder()
                    .baseUrl(WIKI_BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return wikiInstance;
    }

    public static WikipediaApiService getWikiService() {
        return getWikiClient().create(WikipediaApiService.class);
    }
}