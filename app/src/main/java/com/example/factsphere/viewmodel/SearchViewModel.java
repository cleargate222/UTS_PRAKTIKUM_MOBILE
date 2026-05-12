package com.example.factsphere.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.factsphere.model.Fact;
import com.example.factsphere.repository.FactRepository;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private final FactRepository             repository    = new FactRepository();
    private final MutableLiveData<List<Fact>> searchResults = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean>    isSearching   = new MutableLiveData<>(false);
    private final MutableLiveData<String>     noResultMsg   = new MutableLiveData<>();

    public SearchViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Fact>> getSearchResults() { return searchResults; }
    public LiveData<Boolean>    getIsSearching()   { return isSearching; }
    public LiveData<String>     getNoResultMsg()   { return noResultMsg; }

    // Dipanggil setiap user ketik di search bar
    public void search(String query) {

        // Kalau kosong, kosongkan hasil
        if (query == null || query.trim().isEmpty()) {
            searchResults.setValue(new ArrayList<>());
            noResultMsg.setValue(null);
            return;
        }

        isSearching.setValue(true);

        // Gunakan method yang ada di Repository (searchArticles atau getAllFacts)
        repository.searchArticles(query, 20, new FactRepository.FactCallback() {
            @Override
            public void onSuccess(List<Fact> facts) {
                isSearching.setValue(false);
                if (facts == null || facts.isEmpty()) {
                    noResultMsg.setValue("Tidak ada hasil untuk \"" + query + "\"");
                    searchResults.setValue(new ArrayList<>());
                } else {
                    noResultMsg.setValue(null);
                    searchResults.setValue(facts);
                }
            }

            @Override
            public void onFailure(String message) {
                isSearching.setValue(false);
                noResultMsg.setValue("Error: " + message);
            }
        });
    }

    // Filter berdasarkan judul, kategori, atau isi shortFact
    private List<Fact> filterFacts(List<Fact> facts, String query) {
        List<Fact> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (Fact fact : facts) {
            if (fact.getTitle().toLowerCase().contains(lowerQuery) ||
                    fact.getCategory().toLowerCase().contains(lowerQuery) ||
                    fact.getShortFact().toLowerCase().contains(lowerQuery)) {
                filtered.add(fact);
            }
        }
        return filtered;
    }

    // Filter berdasarkan kategori saja (untuk Discover Fragment)
    public void filterByCategory(String category) {
        isSearching.setValue(true);

        repository.searchArticles(category, 20, new FactRepository.FactCallback() {
            @Override
            public void onSuccess(List<Fact> facts) {
                isSearching.setValue(false);
                // Karena kita cari berdasarkan category sebagai query,
                // hasil dari API biasanya sudah terfilter otomatis
                searchResults.setValue(facts);
            }

            @Override
            public void onFailure(String message) {
                isSearching.setValue(false);
            }
        });
    }

    // Reset hasil pencarian
    public void clearSearch() {
        searchResults.setValue(new ArrayList<>());
        noResultMsg.setValue(null);
    }
}