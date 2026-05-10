package com.example.factsphere.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AuthViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> isLoggedIn       = new MutableLiveData<>(false);
    private final MutableLiveData<String>  errorMessage     = new MutableLiveData<>();
    private final MutableLiveData<String>  currentUserEmail = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getIsLoggedIn()       { return isLoggedIn; }
    public LiveData<String>  getErrorMessage()     { return errorMessage; }
    public LiveData<String>  getCurrentUserEmail() { return currentUserEmail; }

    public void login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            errorMessage.setValue("Email dan password tidak boleh kosong");
            return;
        }
        // TODO: sambungkan ke Supabase/Firebase Auth nanti
        currentUserEmail.setValue(email);
        isLoggedIn.setValue(true);
    }

    public void logout() {
        isLoggedIn.setValue(false);
        currentUserEmail.setValue(null);
    }
}