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

    private final FactRepository        repository    = new FactRepository();
    private final MutableLiveData<List<Fact>> searchResults = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean>    isSearching   = new MutableLiveData<>(false);

    public SearchViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Fact>> getSearchResults() { return searchResults; }
    public LiveData<Boolean>    getIsSearching()   { return isSearching; }

    public void search(String query) {
        if (query == null || query.trim().isEmpty()) {
            searchResults.setValue(new ArrayList<>());
            return;
        }
        isSearching.setValue(true);
        repository.searchFacts(query).observeForever(results -> {
            isSearching.setValue(false);
            searchResults.setValue(results != null ? results : new ArrayList<>());
        });
    }

    public void clearSearch() {
        searchResults.setValue(new ArrayList<>());
    }
}