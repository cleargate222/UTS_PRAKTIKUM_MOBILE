package com.example.factsphere.network;

import com.example.factsphere.model.WikipediaResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WikipediaApiService {

    @GET("api/rest_v1/page/summary/{title}")
    Call<WikipediaResponse> getArticleSummary(
            @Path("title") String title
    );
}