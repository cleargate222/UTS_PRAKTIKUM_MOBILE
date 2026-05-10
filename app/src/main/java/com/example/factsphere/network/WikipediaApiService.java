package com.example.factsphere.network;

import com.example.factsphere.model.WikipediaSearchResponse;
import com.example.factsphere.model.WikipediaResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WikipediaApiService {

    // Ambil summary artikel (short fact + thumbnail + link)
    @GET("api/rest_v1/page/summary/{title}")
    Call<WikipediaResponse> getArticleSummary(
            @Path("title") String title
    );

    // Search artikel Wikipedia Bahasa Indonesia
    @GET("w/api.php")
    Call<WikipediaSearchResponse> searchArticles(
            @Query("action") String action,
            @Query("list") String list,
            @Query("srsearch") String query,
            @Query("format") String format,
            @Query("utf8") int utf8,
            @Query("srlimit") int limit
    );
}