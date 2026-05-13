package com.example.factsphere.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.factsphere.supabase.SupabaseClientProvider;

import org.json.JSONObject;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.content.Context;
import android.content.SharedPreferences;

public class AuthViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> isLoggedIn       = new MutableLiveData<>(false);
    private final MutableLiveData<String>  errorMessage     = new MutableLiveData<>();
    private final MutableLiveData<String>  currentUserEmail = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading        = new MutableLiveData<>(false);

    // Simpan token setelah login
    private String accessToken = null;
    // ... variabel lainnya tetap sama
    private final SharedPreferences sharedPreferences;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        // Inisialisasi SharedPreferences
        sharedPreferences = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);

        // Ambil email yang tersimpan (jika ada) saat ViewModel dibuat
        String savedEmail = sharedPreferences.getString("user_email", null);
        if (savedEmail != null) {
            currentUserEmail.setValue(savedEmail);
            isLoggedIn.setValue(true);
        }
    }


    public LiveData<Boolean> getIsLoggedIn()       { return isLoggedIn; }
    public LiveData<String>  getErrorMessage()     { return errorMessage; }
    public LiveData<String>  getCurrentUserEmail() { return currentUserEmail; }
    public LiveData<Boolean> getIsLoading()        { return isLoading; }
    public String            getAccessToken()      { return accessToken; }

    // Login dengan Email + Password
    public void login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            errorMessage.setValue("Email dan password tidak boleh kosong");
            return;
        }

        isLoading.setValue(true);

        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("email", email);
                body.put("password", password);

                RequestBody requestBody = RequestBody.create(
                        body.toString(), SupabaseClientProvider.JSON);

                Request request = SupabaseClientProvider
                        .baseRequest("/auth/v1/token?grant_type=password")
                        .post(requestBody)
                        .build();

                Response response = SupabaseClientProvider
                        .getClient().newCall(request).execute();

                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);

                if (response.isSuccessful()) {
                    accessToken = json.getString("access_token");

                    // SIMPAN EMAIL KE PREFERENCES
                    sharedPreferences.edit().putString("user_email", email).apply();

                    isLoggedIn.postValue(true);
                    currentUserEmail.postValue(email);
                } else {
                    String msg = json.optString("error_description", "Login gagal");
                    errorMessage.postValue(msg);
                }

                isLoading.postValue(false);

            } catch (Exception e) {
                errorMessage.postValue("Login gagal: " + e.getMessage());
                isLoading.postValue(false);
            }
        }).start();
    }

    // Register dengan Email + Password
    public void register(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            errorMessage.setValue("Email dan password tidak boleh kosong");
            return;
        }

        isLoading.setValue(true);

        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("email", email);
                body.put("password", password);

                RequestBody requestBody = RequestBody.create(
                        body.toString(), SupabaseClientProvider.JSON);

                Request request = SupabaseClientProvider
                        .baseRequest("/auth/v1/signup")
                        .post(requestBody)
                        .build();

                Response response = SupabaseClientProvider
                        .getClient().newCall(request).execute();

                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);

                if (response.isSuccessful()) {
                    accessToken = json.optString("access_token", null);
                    isLoggedIn.postValue(true);
                    currentUserEmail.postValue(email);
                } else {
                    String msg = json.optString("error_description", "Register gagal");
                    errorMessage.postValue(msg);
                }

                isLoading.postValue(false);

            } catch (Exception e) {
                errorMessage.postValue("Register gagal: " + e.getMessage());
                isLoading.postValue(false);
            }
        }).start();
    }

    // Logout
    public void logout() {
        isLoading.setValue(true);

        new Thread(() -> {
            try {
                Request request = SupabaseClientProvider
                        .baseRequest("/auth/v1/logout")
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .post(RequestBody.create("", SupabaseClientProvider.JSON))
                        .build();

                SupabaseClientProvider.getClient().newCall(request).execute();

                accessToken = null;
                isLoggedIn.postValue(false);
                currentUserEmail.postValue(null);
                isLoading.postValue(false);

            } catch (Exception e) {
                errorMessage.postValue("Logout gagal: " + e.getMessage());
                isLoading.postValue(false);
            }
        }).start();
    }
}