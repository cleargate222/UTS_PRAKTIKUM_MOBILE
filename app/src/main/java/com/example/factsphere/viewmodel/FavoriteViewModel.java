package com.example.factsphere.viewmodel;

import android.app.Application;
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

    // Ambil semua favorites milik user
    public void loadFavorites(String userId, String accessToken) {
        isLoading.setValue(true);

        new Thread(() -> {
            try {
                Request request = SupabaseClientProvider
                        .baseRequest("/rest/v1/favorites?user_id=eq." + userId + "&select=*")
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .get()
                        .build();

                Response response = SupabaseClientProvider
                        .getClient().newCall(request).execute();

                String responseBody = response.body().string();
                JSONArray array = new JSONArray(responseBody);

                List<Fact> facts = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    facts.add(new Fact(
                            obj.getString("fact_id"),
                            obj.getString("title"),
                            obj.getString("short_fact"),
                            "",
                            obj.getString("category"),
                            ""
                    ));
                }

                favoriteList.postValue(facts);
                isLoading.postValue(false);

            } catch (Exception e) {
                errorMessage.postValue("Gagal memuat favorit: " + e.getMessage());
                isLoading.postValue(false);
            }
        }).start();
    }

    // Tambah favorite
    public void addFavorite(String userId, String accessToken, Fact fact) {
        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("user_id",    userId);
                body.put("fact_id",    fact.getId());
                body.put("title",      fact.getTitle());
                body.put("short_fact", fact.getShortFact());
                body.put("category",   fact.getCategory());

                RequestBody requestBody = RequestBody.create(
                        body.toString(), SupabaseClientProvider.JSON);

                Request request = SupabaseClientProvider
                        .baseRequest("/rest/v1/favorites")
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .post(requestBody)
                        .build();

                Response response = SupabaseClientProvider
                        .getClient().newCall(request).execute();

                if (response.isSuccessful()) {
                    List<Fact> current = favoriteList.getValue();
                    if (current == null) current = new ArrayList<>();
                    List<Fact> updated = new ArrayList<>(current);
                    updated.add(fact);
                    favoriteList.postValue(updated);
                } else {
                    errorMessage.postValue("Gagal menambah favorit");
                }

            } catch (Exception e) {
                errorMessage.postValue("Gagal menambah favorit: " + e.getMessage());
            }
        }).start();
    }

    // Hapus favorite
    public void removeFavorite(String userId, String accessToken, String factId) {
        new Thread(() -> {
            try {
                Request request = SupabaseClientProvider
                        .baseRequest("/rest/v1/favorites?user_id=eq."
                                + userId + "&fact_id=eq." + factId)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .delete()
                        .build();

                Response response = SupabaseClientProvider
                        .getClient().newCall(request).execute();

                if (response.isSuccessful()) {
                    List<Fact> current = favoriteList.getValue();
                    if (current == null) return;
                    List<Fact> updated = new ArrayList<>();
                    for (Fact f : current) {
                        if (!f.getId().equals(factId)) updated.add(f);
                    }
                    favoriteList.postValue(updated);
                } else {
                    errorMessage.postValue("Gagal menghapus favorit");
                }

            } catch (Exception e) {
                errorMessage.postValue("Gagal menghapus favorit: " + e.getMessage());
            }
        }).start();
    }

    // Toggle favorite
    public void toggleFavorite(String userId, String accessToken, Fact fact) {
        if (isFavorite(fact.getId())) {
            removeFavorite(userId, accessToken, fact.getId());
        } else {
            addFavorite(userId, accessToken, fact);
        }
    }

    // Cek apakah fakta sudah di-favorite
    public boolean isFavorite(String factId) {
        List<Fact> current = favoriteList.getValue();
        if (current == null) return false;
        for (Fact f : current) {
            if (f.getId().equals(factId)) return true;
        }
        return false;
    }
}