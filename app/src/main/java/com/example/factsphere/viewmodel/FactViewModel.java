package com.example.factsphere.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.factsphere.model.Fact;
import com.example.factsphere.repository.FactRepository;

import java.util.List;

public class FactViewModel extends AndroidViewModel {

    private final FactRepository repository;
    private final MutableLiveData<List<Fact>> factList     = new MutableLiveData<>();
    private final MutableLiveData<Boolean>    isLoading    = new MutableLiveData<>(false);
    private final MutableLiveData<String>     errorMessage = new MutableLiveData<>();

    public FactViewModel(@NonNull Application application) {
        super(application);
        repository = new FactRepository();
    }

    public LiveData<List<Fact>> getFactList()     { return factList; }
    public LiveData<Boolean>    getIsLoading()    { return isLoading; }
    public LiveData<String>     getErrorMessage() { return errorMessage; }

    public void loadFacts() {
        isLoading.setValue(true);
        repository.getAllFacts().observeForever(facts -> {
            isLoading.setValue(false);
            if (facts != null) {
                factList.setValue(facts);
            } else {
                errorMessage.setValue("Gagal memuat fakta");
            }
        });
    }

    // Untuk Fact of the Day di Home
    public LiveData<Fact> getRandomFact() {
        MutableLiveData<Fact> randomFact = new MutableLiveData<>();
        repository.getAllFacts().observeForever(facts -> {
            if (facts != null && !facts.isEmpty()) {
                int index = (int) (Math.random() * facts.size());
                randomFact.setValue(facts.get(index));
            }
        });
        return randomFact;
    }
}