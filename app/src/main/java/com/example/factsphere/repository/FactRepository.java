package com.example.factsphere.repository;

import com.example.factsphere.model.Fact;
import com.example.factsphere.model.NinjaFactResponse;
import com.example.factsphere.model.WikipediaResponse;
import com.example.factsphere.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FactRepository {

    public interface FactCallback {
        void onSuccess(List<Fact> facts);
        void onFailure(String errorMessage);
    }

    public interface ArticleCallback {
        void onSuccess(String longArticle, String imageUrl);
        void onFailure(String errorMessage);
    }

    public void getRandomFacts(int limit, FactCallback callback) {
        RetrofitClient.getFactsService()
                .getRandomFacts(limit)
                .enqueue(new Callback<List<NinjaFactResponse>>() {
                    @Override
                    public void onResponse(Call<List<NinjaFactResponse>> call,
                                           Response<List<NinjaFactResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(mapToFacts(response.body(), "general"));
                        } else {
                            callback.onFailure("Gagal ambil data: " + response.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<List<NinjaFactResponse>> call, Throwable t) {
                        callback.onFailure("Error: " + t.getMessage());
                    }
                });
    }

    public void getFactsByCategory(String category, int limit, FactCallback callback) {
        RetrofitClient.getFactsService()
                .getFactsByCategory(category, limit)
                .enqueue(new Callback<List<NinjaFactResponse>>() {
                    @Override
                    public void onResponse(Call<List<NinjaFactResponse>> call,
                                           Response<List<NinjaFactResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(mapToFacts(response.body(), category));
                        } else {
                            callback.onFailure("Gagal ambil kategori: " + response.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<List<NinjaFactResponse>> call, Throwable t) {
                        callback.onFailure("Error: " + t.getMessage());
                    }
                });
    }

    public void getLongArticle(String wikipediaTitle, ArticleCallback callback) {
        RetrofitClient.getWikiService()
                .getArticleSummary(wikipediaTitle)
                .enqueue(new Callback<WikipediaResponse>() {
                    @Override
                    public void onResponse(Call<WikipediaResponse> call,
                                           Response<WikipediaResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            WikipediaResponse wiki = response.body();
                            String imageUrl = (wiki.getThumbnail() != null)
                                    ? wiki.getThumbnail().getSource() : null;
                            callback.onSuccess(wiki.getExtract(), imageUrl);
                        } else {
                            callback.onFailure("Artikel tidak ditemukan");
                        }
                    }
                    @Override
                    public void onFailure(Call<WikipediaResponse> call, Throwable t) {
                        callback.onFailure("Error: " + t.getMessage());
                    }
                });
    }

    private List<Fact> mapToFacts(List<NinjaFactResponse> responses, String category) {
        List<Fact> facts = new ArrayList<>();
        for (NinjaFactResponse r : responses) {
            String text = r.getFact();
            String[] words = text.split(" ");
            StringBuilder titleBuilder = new StringBuilder();
            for (int i = 0; i < Math.min(3, words.length); i++) {
                titleBuilder.append(words[i]).append(" ");
            }
            Fact fact = new Fact(
                    UUID.randomUUID().toString(),
                    titleBuilder.toString().trim() + "...",
                    text,
                    category,
                    null,
                    null
            );
            facts.add(fact);
        }
        return facts;
    }
}