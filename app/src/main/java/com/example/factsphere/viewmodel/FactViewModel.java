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

    private final MutableLiveData<List<Fact>> factList     = new MutableLiveData<>();
    private final MutableLiveData<Fact>randomFact = new MutableLiveData<>();
    private final MutableLiveData<Boolean>    isLoading    = new MutableLiveData<>(false);
    private final MutableLiveData<String>     errorMessage = new MutableLiveData<>();

    public FactViewModel(@NonNull Application application) {
        super(application);
        repository = new FactRepository();
    }

    public LiveData<List<Fact>> getFactList()     { return factList; }
    public LiveData<Fact> getRandomFact()     { return randomFact; }
    public LiveData<Boolean>    getIsLoading()    { return isLoading; }
    public LiveData<String>     getErrorMessage() { return errorMessage; }

    public void loadFact(String title) {
        isLoading.setValue(true);

        repository.getArticleDetail(title, new FactRepository.ArticleCallback() {
            @Override
            public void onSuccess(String shortFact, String longArticle, String imageUrl, String articleUrl) {
                isLoading.postValue(false);

                // Kita bungkus data yang didapat ke dalam list agar bisa diterima FactAdapter
                List<Fact> facts = new ArrayList<>();
                facts.add(new Fact(
                        UUID.randomUUID().toString(),
                        title,       // Judul
                        shortFact,   // Isi Ringkas
                        "Featured",  // Kategori
                        imageUrl,    // Gambar (Sudah tidak null lagi!)
                        title        // Wikipedia ID
                ));

                factList.postValue(facts);
            }

            @Override
            public void onFailure(String Message) {
                isLoading.postValue(false);
                errorMessage.postValue(Message);
            }
        });
    }

    // Untuk Fact of the Day di Home
    public void loadRandomFact() {
        isLoading.setValue(true);

        // Gunakan searchArticles untuk mendapatkan LIST artikel
        repository.searchArticles("Teknologi", 10, new FactRepository.FactCallback() {
            @Override
            public void onSuccess(List<Fact> facts) {
                isLoading.postValue(false);
                if (facts != null && !facts.isEmpty()) {
                    // Kirim list ke HomeFragment melalui factList
                    factList.postValue(facts);

                    // Set satu fakta acak untuk fitur 'Fact of the Day'
                    int index = (int) (Math.random() * facts.size());
                    randomFact.postValue(facts.get(index));
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