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
    private final MutableLiveData<String>     toastMessage = new MutableLiveData<>();

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Fact>> getFavorites()    { return favoriteList; }
    public LiveData<String>     getErrorMessage() { return errorMessage; }
    public LiveData<Boolean>    getIsLoading()    { return isLoading; }
    public LiveData<String>     getToastMessage() { return toastMessage; }

    // Load semua favorites dari Supabase
    public void loadFavorites(String userId, String accessToken) {
        if (userId == null || accessToken == null) return;
        isLoading.setValue(true);

        new Thread(() -> {
            try {
                Request request = SupabaseClientProvider
                        .baseRequest("/rest/v1/favorites?user_id=eq."
                                + userId + "&select=*")
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .get()
                        .build();

                Response response = SupabaseClientProvider
                        .getClient().newCall(request).execute();

                String body = response.body().string();
                android.util.Log.d("FAVORITE_DEBUG",
                        "loadFavorites response: " + body);

                if (response.isSuccessful()) {
                    JSONArray array = new JSONArray(body);
                    List<Fact> facts = new ArrayList<>();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        Fact fact = new Fact(
                                obj.getString("fact_id"),
                                obj.getString("title"),
                                obj.getString("short_fact"),
                                "Umum",
                                "",
                                obj.getString("title")
                        );
                        fact.setFavorite(true);
                        facts.add(fact);
                    }
                    favoriteList.postValue(facts);
                } else {
                    errorMessage.postValue("Gagal load: " + body);
                }
                isLoading.postValue(false);

            } catch (Exception e) {
                errorMessage.postValue("Error: " + e.getMessage());
                isLoading.postValue(false);
            }
        }).start();
    }

    // Tambah favorite ke Supabase
    public void addFavorite(String userId, String accessToken, Fact fact) {
        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("user_id",    userId);
                body.put("fact_id",    fact.getId());
                body.put("title",      fact.getTitle());
                body.put("short_fact", fact.getShortFact());
                body.put("category",   fact.getCategory() != null
                        ? fact.getCategory() : "Umum");

                Request request = SupabaseClientProvider
                        .baseRequest("/rest/v1/favorites")
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .addHeader("Prefer", "return=minimal")
                        .post(RequestBody.create(
                                body.toString(),
                                SupabaseClientProvider.JSON))
                        .build();

                Response response = SupabaseClientProvider
                        .getClient().newCall(request).execute();

                android.util.Log.d("FAVORITE_DEBUG",
                        "addFavorite status: " + response.code());

                if (response.isSuccessful()) {
                    // Update list lokal
                    List<Fact> current = new ArrayList<>(
                            favoriteList.getValue() != null
                                    ? favoriteList.getValue() : new ArrayList<>());
                    fact.setFavorite(true);
                    current.add(fact);
                    favoriteList.postValue(current);
                    toastMessage.postValue("Disimpan ke favorit ❤️");
                } else {
                    String respBody = response.body().string();
                    android.util.Log.e("FAVORITE_DEBUG",
                            "addFavorite error: " + respBody);
                    errorMessage.postValue("Gagal menyimpan");
                }

            } catch (Exception e) {
                errorMessage.postValue("Error: " + e.getMessage());
            }
        }).start();
    }

    // Hapus favorite dari Supabase
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

                android.util.Log.d("FAVORITE_DEBUG",
                        "removeFavorite status: " + response.code());

                if (response.isSuccessful()) {
                    List<Fact> current = new ArrayList<>(
                            favoriteList.getValue() != null
                                    ? favoriteList.getValue() : new ArrayList<>());
                    current.removeIf(f -> f.getId().equals(factId));
                    favoriteList.postValue(current);
                    toastMessage.postValue("Dihapus dari favorit");
                } else {
                    errorMessage.postValue("Gagal menghapus");
                }

            } catch (Exception e) {
                errorMessage.postValue("Error: " + e.getMessage());
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

    // Cek status favorite
    public boolean isFavorite(String factId) {
        List<Fact> current = favoriteList.getValue();
        if (current == null) return false;
        for (Fact f : current) {
            if (f.getId().equals(factId)) return true;
        }
        return false;
    }
}