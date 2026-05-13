package com.example.factsphere.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.factsphere.model.Fact;
import com.example.factsphere.repository.FactRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SearchViewModel extends ViewModel {

    private final FactRepository repository;
    private final MutableLiveData<List<Fact>> searchResults = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isSearching = new MutableLiveData<>(false);
    private final MutableLiveData<String> noResultMsg = new MutableLiveData<>();

    public SearchViewModel() {
        this.repository = new FactRepository();
    }

    public LiveData<List<Fact>> getSearchResults() { return searchResults; }
    public LiveData<Boolean> getIsSearching() { return isSearching; }
    public LiveData<String> getNoResultMsg() { return noResultMsg; }

    public void search(String query) {
        repository.searchArticles(query, 10, new FactRepository.SearchCallback() {
            @Override
            public void onSuccess(List<String> titles) {
                List<Fact> factList = new ArrayList<>();
                for (String title : titles) {
                    // Ambil detail untuk setiap judul yang ditemukan
                    repository.getArticleDetail(title, new FactRepository.ArticleCallback() {
                        @Override
                        public void onSuccess(String shortFact, String longArticle, String imageUrl, String wikipediaTitle) {
                            factList.add(new Fact(null, title, shortFact, "General", imageUrl, wikipediaTitle));
                            // Update LiveData setiap kali ada data baru masuk
                            searchResults.postValue(new ArrayList<>(factList));
                        }

                        @Override
                        public void onFailure(String message) { /* Handle error */ }
                    });
                }
            }

            @Override
            public void onFailure(String message) { /* Handle error */ }
        });
    }
}