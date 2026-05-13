package com.example.factsphere.supabase;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.MediaType;

public class SupabaseClientProvider {

    public static final String SUPABASE_URL = "https://rtfrladgvryqwitmzujv.supabase.co";
    // Anon Key digunakan sebagai apikey tetap
    public static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJ0ZnJsYWRndnJ5cXdpdG16dWp2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzg0MTA1NTksImV4cCI6MjA5Mzk4NjU1OX0.0_r4LIqsDY7LaAKFE15Cqm_7EO4UizorcT8Z6z5dp8M";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static OkHttpClient client = null;

    public static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }

    public static Request.Builder baseRequest(String endpoint) {
        return new Request.Builder()
                .url(SUPABASE_URL + endpoint)
                .addHeader("apikey", SUPABASE_KEY)
                // Header Authorization dihapus dari sini agar bisa diatur secara dinamis di ViewModel
                .addHeader("Content-Type", "application/json");
    }
}