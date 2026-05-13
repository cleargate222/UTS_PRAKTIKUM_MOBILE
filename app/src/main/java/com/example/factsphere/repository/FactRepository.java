package com.example.factsphere.repository;

import androidx.annotation.NonNull;
import com.example.factsphere.model.WikipediaResponse;
import com.example.factsphere.model.WikipediaSearchResponse;
import com.example.factsphere.network.RetrofitClient;
import com.example.factsphere.network.WikipediaApiService;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FactRepository {
    private final WikipediaApiService apiService;

    public interface SearchCallback {
        void onSuccess(List<String> titles);
        void onFailure(String message);
    }

    public interface ArticleCallback {
        void onSuccess(String shortFact, String longArticle, String imageUrl, String wikipediaTitle);
        void onFailure(String message);
    }

    public FactRepository() {
        apiService = RetrofitClient.getWikiService();
    }

    public void searchArticles(String query, int limit, SearchCallback callback) {
        // Parameter "query" dan "search" harus sesuai dengan kebutuhan API Wikipedia
        apiService.searchArticles("query", "search", query, "json", 1, limit)
                .enqueue(new Callback<WikipediaSearchResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<WikipediaSearchResponse> call, @NonNull Response<WikipediaSearchResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getQuery() != null) {
                            List<String> titles = new ArrayList<>();
                            for (WikipediaSearchResponse.SearchResult item : response.body().getQuery().getSearch()) {
                                titles.add(item.getTitle());
                            }
                            callback.onSuccess(titles);
                        } else {
                            callback.onFailure("Gagal mendapatkan daftar artikel");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<WikipediaSearchResponse> call, @NonNull Throwable t) {
                        callback.onFailure(t.getMessage());
                    }
                });
    }

    public void getArticleDetail(String title, ArticleCallback callback) {
        apiService.getArticleSummary(title).enqueue(new Callback<WikipediaResponse>() {
            @Override
            public void onResponse(@NonNull Call<WikipediaResponse> call, @NonNull Response<WikipediaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WikipediaResponse wiki = response.body();
                    String imgUrl = (wiki.getThumbnail() != null) ? wiki.getThumbnail().getSource() : null;
                    // Kirim judul asli (wiki.getTitle()) agar link sumber nanti valid
                    callback.onSuccess(wiki.getExtract(), wiki.getExtract(), imgUrl, wiki.getTitle());
                } else {
                    callback.onFailure("Gagal muat detail");
                }
            }

            @Override
            public void onFailure(@NonNull Call<WikipediaResponse> call, @NonNull Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }
}