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

        repository.getAllFacts().observeForever(facts -> {
            if (facts == null) {
                isSearching.postValue(false);
                return;
            }

            List<Fact> filtered = filterFacts(facts, query.trim());

            if (filtered.isEmpty()) {
                noResultMsg.postValue("Tidak ada hasil untuk \"" + query + "\"");
            } else {
                noResultMsg.postValue(null);
            }

            searchResults.postValue(filtered);
            isSearching.postValue(false);
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

        repository.getAllFacts().observeForever(facts -> {
            if (facts == null) {
                isSearching.postValue(false);
                return;
            }

            List<Fact> filtered = new ArrayList<>();
            for (Fact fact : facts) {
                if (fact.getCategory().equalsIgnoreCase(category)) {
                    filtered.add(fact);
                }
            }

            searchResults.postValue(filtered);
            isSearching.postValue(false);
        });
    }

    // Reset hasil pencarian
    public void clearSearch() {
        searchResults.setValue(new ArrayList<>());
        noResultMsg.setValue(null);
    }
}