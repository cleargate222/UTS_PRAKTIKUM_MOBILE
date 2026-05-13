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
import java.util.UUID;

public class FactViewModel extends AndroidViewModel {
    private final FactRepository repository;

    // LiveData yang dipanggil di HomeFragment
    private final MutableLiveData<List<Fact>> factList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public FactViewModel(@NonNull Application application) {
        super(application);
        repository = new FactRepository();
    }

    // Getter untuk dipanggil di Fragment (Baris 43 & 51 di image_9c0546.jpg)
    public LiveData<List<Fact>> getFactList() { return factList; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadMultipleFacts(String query) {
        isLoading.setValue(true);
        List<Fact> currentList = new ArrayList<>();

        repository.searchArticles(query, 15, new FactRepository.SearchCallback() {
            @Override
            public void onSuccess(List<String> titles) {
                if (titles.isEmpty()) {
                    isLoading.postValue(false);
                    errorMessage.postValue("Tidak ada data ditemukan.");
                    return;
                }

                for (String title : titles) {
                    repository.getArticleDetail(title, new FactRepository.ArticleCallback() {
                        @Override
                        public void onSuccess(String shortFact, String longArticle, String imageUrl, String articleUrl) {
                            currentList.add(new Fact(UUID.randomUUID().toString(), title, shortFact, "Discovery", imageUrl, title));

                            // Update list ke UI
                            factList.postValue(new ArrayList<>(currentList));

                            if (currentList.size() >= titles.size()) {
                                isLoading.postValue(false);
                            }
                        }

                        @Override
                        public void onFailure(String message) {
                            // Jika satu detail gagal, kita biarkan yang lain tetap jalan
                        }
                    });
                }
            }

            @Override
            public void onFailure(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }
}