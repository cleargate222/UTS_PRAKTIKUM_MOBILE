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

    // Ambil detail artikel (short fact + long article + thumbnail + link)
    public void getArticleDetail(String wikipediaTitle, ArticleCallback callback) {
        RetrofitClient.getWikiService()
                .getArticleSummary(wikipediaTitle)
                .enqueue(new Callback<WikipediaResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<WikipediaResponse> call,
                                           @NonNull Response<WikipediaResponse> response) {
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
                            callback.onFailure("Artikel tidak ditemukan");
                        }
                    }
                    @Override
                    public void onFailure(Call<WikipediaResponse> call, Throwable t) {
                        callback.onFailure("Error: " + t.getMessage());
                    }
                });
    }

    // Search artikel Wikipedia
    public void searchArticles(String query, int limit, FactCallback callback) {
        RetrofitClient.getWikiService()
                .searchArticles("query", "search", query ,"json", 1, limit)
                .enqueue(new Callback<WikipediaSearchResponse>() {
                    @Override
                    public void onResponse(Call<WikipediaSearchResponse>call,
                                           Response<WikipediaSearchResponse> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getQuery() != null) {
                            List<Fact> facts = mapSearchToFacts(
                                    response.body().getQuery().getSearch()
                            );
                            callback.onSuccess(facts);
                        } else {
                            callback.onFailure("Hasil pencarian kosong");
                        }
                    }
                    @Override
                    public void onFailure(Call<WikipediaSearchResponse> call, Throwable t) {
                        callback.onFailure("Error: " + t.getMessage());
                    }
                });
    }

    // Helper: ubah SearchResult → Fact
    private List<Fact> mapSearchToFacts(List<WikipediaSearchResponse.SearchResult> results) {
        List<Fact> facts = new ArrayList<>();
        if (results == null) return facts;
        for (WikipediaSearchResponse.SearchResult r : results) {
            // Hapus tag HTML dari snippet
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