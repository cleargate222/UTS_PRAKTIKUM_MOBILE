package com.example.factsphere.repository;

import androidx.annotation.NonNull;

import com.example.factsphere.model.Fact;
import com.example.factsphere.model.WikipediaResponse;
import com.example.factsphere.model.WikipediaSearchResponse;
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
        void onSuccess(String shortFact, String longArticle, String imageUrl, String articleUrl);
        void onFailure(String errorMessage);
    }

    public void getArticleDetail(String wikipediaTitle, ArticleCallback callback) {
        android.util.Log.d("FactRepository", "getArticleDetail dipanggil: " + wikipediaTitle);
        RetrofitClient.getWikiService()
                .getArticleSummary(wikipediaTitle)
                .enqueue(new Callback<WikipediaResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<WikipediaResponse> call,
                                           @NonNull Response<WikipediaResponse> response) {
                        android.util.Log.d("FactRepository", "getArticleDetail response: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            WikipediaResponse wiki = response.body();
                            String shortFact = wiki.getExtract() != null
                                    ? wiki.getExtract().substring(0, Math.min(200, wiki.getExtract().length())) + "..."
                                    : "Tidak ada deskripsi";
                            String longArticle = wiki.getExtract() != null
                                    ? wiki.getExtract()
                                    : "Artikel tidak tersedia";
                            String imageUrl = (wiki.getThumbnail() != null)
                                    ? wiki.getThumbnail().getSource() : null;
                            String articleUrl = (wiki.getContentUrls() != null
                                    && wiki.getContentUrls().getDesktop() != null)
                                    ? wiki.getContentUrls().getDesktop().getPage() : null;
                            callback.onSuccess(shortFact, longArticle, imageUrl, articleUrl);
                        } else {
                            android.util.Log.e("FactRepository", "getArticleDetail gagal: " + response.code());
                            callback.onFailure("Artikel tidak ditemukan");
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<WikipediaResponse> call, @NonNull Throwable t) {
                        android.util.Log.e("FactRepository", "getArticleDetail error: " + t.getMessage());
                        callback.onFailure("Error: " + t.getMessage());
                    }
                });
    }

    public void searchArticles(String query, int limit, FactCallback callback) {
        android.util.Log.d("FactRepository", "searchArticles dipanggil: " + query);
        RetrofitClient.getWikiService()
                .searchArticles("query", "search", query, "json", 1, limit)
                .enqueue(new Callback<WikipediaSearchResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<WikipediaSearchResponse> call,
                                           @NonNull Response<WikipediaSearchResponse> response) {
                        android.util.Log.d("FactRepository", "searchArticles response: " + response.code());
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getQuery() != null) {
                            List<Fact> facts = mapSearchToFacts(
                                    response.body().getQuery().getSearch()
                            );
                            android.util.Log.d("FactRepository", "searchArticles hasil: " + facts.size() + " fakta");
                            callback.onSuccess(facts);
                        } else {
                            android.util.Log.e("FactRepository", "searchArticles kosong");
                            callback.onFailure("Hasil pencarian kosong");
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<WikipediaSearchResponse> call, @NonNull Throwable t) {
                        android.util.Log.e("FactRepository", "searchArticles error: " + t.getMessage());
                        callback.onFailure("Error: " + t.getMessage());
                    }
                });
    }

    public void getMultipleArticles(List<String> titles, FactCallback callback) {
        android.util.Log.d("FactRepository", "getMultipleArticles dipanggil, jumlah: " + titles.size());
        List<Fact> allFacts = new ArrayList<>();
        int[] count = {0};
        for (String title : titles) {
            android.util.Log.d("FactRepository", "Fetching: " + title);
            RetrofitClient.getWikiService()
                    .getArticleSummary(title)
                    .enqueue(new Callback<WikipediaResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<WikipediaResponse> call,
                                               @NonNull Response<WikipediaResponse> response) {
                            count[0]++;
                            android.util.Log.d("FactRepository", "Response " + title + ": " + response.code());
                            if (response.isSuccessful() && response.body() != null) {
                                WikipediaResponse wiki = response.body();
                                String shortFact = wiki.getExtract() != null
                                        ? wiki.getExtract().substring(0, Math.min(200, wiki.getExtract().length())) + "..."
                                        : "Tidak ada deskripsi";
                                Fact fact = new Fact(
                                        UUID.randomUUID().toString(),
                                        wiki.getTitle(),
                                        shortFact,
                                        "general",
                                        wiki.getThumbnail() != null ? wiki.getThumbnail().getSource() : null,
                                        wiki.getTitle()
                                );
                                allFacts.add(fact);
                                android.util.Log.d("FactRepository", "Berhasil: " + wiki.getTitle());
                            } else {
                                android.util.Log.e("FactRepository", "Gagal " + title + ": " + response.code());
                            }
                            if (count[0] == titles.size()) {
                                android.util.Log.d("FactRepository", "Semua selesai, total: " + allFacts.size());
                                callback.onSuccess(allFacts);
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<WikipediaResponse> call, @NonNull Throwable t) {
                            count[0]++;
                            android.util.Log.e("FactRepository", "Error " + title + ": " + t.getMessage());
                            if (count[0] == titles.size()) {
                                callback.onFailure("Error: " + t.getMessage());
                            }
                        }
                    });
        }
    }

    private List<Fact> mapSearchToFacts(List<WikipediaSearchResponse.SearchResult> results) {
        List<Fact> facts = new ArrayList<>();
        if (results == null) return facts;
        for (WikipediaSearchResponse.SearchResult r : results) {
            String snippet = r.getSnippet().replaceAll("<[^>]*>", "");
            Fact fact = new Fact(
                    UUID.randomUUID().toString(),
                    r.getTitle(),
                    snippet,
                    "general",
                    null,
                    r.getTitle()
            );
            facts.add(fact);
        }
        return facts;
    }
}