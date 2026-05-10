package com.example.factsphere.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.factsphere.model.Fact;

import java.util.ArrayList;
import java.util.List;

public class FavoriteViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Fact>> favoriteList = new MutableLiveData<>(new ArrayList<>());

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        // Nanti: load dari Supabase di sini
    }

    public LiveData<List<Fact>> getFavorites() { return favoriteList; }

    public void toggleFavorite(Fact fact) {
        List<Fact> current = favoriteList.getValue();
        if (current == null) current = new ArrayList<>();

        // Buat list baru (hindari mutasi langsung)
        List<Fact> updated = new ArrayList<>(current);

        boolean found = false;
        for (int i = 0; i < updated.size(); i++) {
            if (updated.get(i).getId().equals(fact.getId())) {
                updated.remove(i);
                found = true;
                break;
            }
        }
        if (!found) updated.add(fact);

        favoriteList.setValue(updated);
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