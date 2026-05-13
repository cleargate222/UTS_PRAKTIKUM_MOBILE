package com.example.factsphere.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.factsphere.model.Fact;
import com.example.factsphere.supabase.SupabaseClientProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FavoriteViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Fact>> favoriteList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String>     errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean>    isLoading    = new MutableLiveData<>(false);

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Fact>> getFavorites()    { return favoriteList; }
    public LiveData<String>     getErrorMessage() { return errorMessage; }
    public LiveData<Boolean>    getIsLoading()    { return isLoading; }

    public void loadFavorites(String userEmail, String accessToken) {
        if (accessToken == null || userEmail == null) return;
        isLoading.setValue(true);
        new Thread(() -> {
            try {
                String encodedEmail = userEmail.replace("@", "%40");
                Request request = SupabaseClientProvider
                        .baseRequest("/rest/v1/favorites?user_id=eq." + encodedEmail + "&select=*")
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .get()
                        .build();

                Response response = SupabaseClientProvider.getClient().newCall(request).execute();
                String responseBody = response.body() != null ? response.body().string() : "[]";

                if (response.isSuccessful()) {
                    JSONArray array = new JSONArray(responseBody);
                    List<Fact> facts = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        facts.add(new Fact(
                                obj.getString("fact_id"),
                                obj.getString("title"),
                                obj.getString("short_fact"),
                                // Tetap set kosong karena kolom tidak ada di DB
                                "",
                                obj.getString("category"),
                                ""
                        ));
                    }
                    favoriteList.postValue(facts);
                }
                isLoading.postValue(false);
            } catch (Exception e) {
                errorMessage.postValue("Gagal memuat: " + e.getMessage());
                isLoading.postValue(false);
            }
        }).start();
    }

    public void addFavorite(String userEmail, String accessToken, Fact fact) {
        if (accessToken == null) {
            Log.e("SUPABASE_ERROR", "Token null, gagal simpan");
            return;
        }

        new Thread(() -> {
            try {
                // HANYA mengirim kolom yang pasti ada di tabel favorites kamu
                JSONObject body = new JSONObject();
                body.put("user_id", userEmail);
                body.put("fact_id", fact.getId());
                body.put("title", fact.getTitle());
                body.put("short_fact", fact.getShortFact());
                body.put("category", fact.getCategory());
                // Baris image_url sudah dihapus sesuai permintaan

                RequestBody requestBody = RequestBody.create(body.toString(), SupabaseClientProvider.JSON);

                Request request = SupabaseClientProvider.baseRequest("/rest/v1/favorites")
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .addHeader("Prefer", "return=minimal")
                        .post(requestBody)
                        .build();

                Response response = SupabaseClientProvider.getClient().newCall(request).execute();

                if (response.isSuccessful()) {
                    Log.d("SUPABASE_SUCCESS", "Data tersimpan tanpa image_url");
                    List<Fact> current = favoriteList.getValue();
                    List<Fact> updated = new ArrayList<>(current != null ? current : new ArrayList<>());
                    updated.add(fact);
                    favoriteList.postValue(updated);
                } else {
                    String errorLog = response.body() != null ? response.body().string() : "No error body";
                    Log.e("SUPABASE_ERROR", "Status: " + response.code() + " | Detail: " + errorLog);
                    errorMessage.postValue("Gagal menyimpan ke database");
                }
            } catch (Exception e) {
                Log.e("SUPABASE_CRASH", e.getMessage());
            }
        }).start();
    }

    public void removeFavorite(String userEmail, String accessToken, String factId) {
        new Thread(() -> {
            try {
                String encodedEmail = userEmail.replace("@", "%40");
                Request request = SupabaseClientProvider
                        .baseRequest("/rest/v1/favorites?user_id=eq." + encodedEmail + "&fact_id=eq." + factId)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .delete()
                        .build();

                Response response = SupabaseClientProvider.getClient().newCall(request).execute();
                if (response.isSuccessful()) {
                    List<Fact> current = favoriteList.getValue();
                    if (current == null) return;
                    List<Fact> updated = new ArrayList<>();
                    for (Fact f : current) {
                        if (!f.getId().equals(factId)) updated.add(f);
                    }
                    favoriteList.postValue(updated);
                }
            } catch (Exception e) {
                Log.e("SUPABASE_ERROR", "Gagal hapus: " + e.getMessage());
            }
        }).start();
    }

    public void toggleFavorite(String userEmail, String accessToken, Fact fact) {
        if (isFavorite(fact.getId())) {
            removeFavorite(userEmail, accessToken, fact.getId());
        } else {
            addFavorite(userEmail, accessToken, fact);
        }
    }

    public boolean isFavorite(String factId) {
        List<Fact> current = favoriteList.getValue();
        if (current == null) return false;
        for (Fact f : current) {
            if (f.getId().equals(factId)) return true;
        }
        return false;
    }
}