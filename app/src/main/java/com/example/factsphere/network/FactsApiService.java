package com.example.factsphere.network;

import com.example.factsphere.model.NinjaFactResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FactsApiService {

    @GET("v1/facts")
    Call<List<NinjaFactResponse>> getRandomFacts(
            @Query("limit") int limit
    );

    @GET("v1/facts")
    Call<List<NinjaFactResponse>> getFactsByCategory(
            @Query("category") String category,
            @Query("limit")    int limit
    );
}