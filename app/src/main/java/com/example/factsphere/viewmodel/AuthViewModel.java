package com.example.factsphere.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.factsphere.supabase.SupabaseClientProvider;
import org.json.JSONObject;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> isLoggedIn       = new MutableLiveData<>(false);
    private final MutableLiveData<String>  errorMessage     = new MutableLiveData<>();
    private final MutableLiveData<String>  currentUserEmail = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading        = new MutableLiveData<>(false);

    private String accessToken = null;
    private final SharedPreferences sharedPreferences;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        // Tetap menggunakan "auth_prefs" agar konsisten
        sharedPreferences = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);

        // 1. Ambil email DAN token yang tersimpan saat ViewModel dibuat
        String savedEmail = sharedPreferences.getString("user_email", null);
        String savedToken = sharedPreferences.getString("access_token", null);

        if (savedEmail != null && savedToken != null) {
            this.accessToken = savedToken;
            currentUserEmail.setValue(savedEmail);
            isLoggedIn.setValue(true);
        }
    }

    public LiveData<Boolean> getIsLoggedIn()       { return isLoggedIn; }
    public LiveData<String>  getErrorMessage()     { return errorMessage; }
    public LiveData<String>  getCurrentUserEmail() { return currentUserEmail; }
    public LiveData<Boolean> getIsLoading()        { return isLoading; }

    // 2. Sesuaikan getter agar selalu mengecek SharedPreferences jika variabel null
    public String getAccessToken() {
        if (accessToken == null) {
            accessToken = sharedPreferences.getString("access_token", null);
        }
        return accessToken;
    }

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

                    // 3. SIMPAN TOKEN DAN EMAIL KE PREFERENCES
                    sharedPreferences.edit()
                            .putString("user_email", email)
                            .putString("access_token", accessToken)
                            .apply();

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
                    // Jika signup langsung memberikan token
                    accessToken = json.optString("access_token", null);

                    if (accessToken != null) {
                        sharedPreferences.edit()
                                .putString("user_email", email)
                                .putString("access_token", accessToken)
                                .apply();
                    }

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

    public void logout() {
        isLoading.setValue(true);
        new Thread(() -> {
            try {
                if (getAccessToken() != null) {
                    Request request = SupabaseClientProvider
                            .baseRequest("/auth/v1/logout")
                            .addHeader("Authorization", "Bearer " + getAccessToken())
                            .post(RequestBody.create("", SupabaseClientProvider.JSON))
                            .build();
                    SupabaseClientProvider.getClient().newCall(request).execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 4. Bersihkan SharedPreferences saat logout
                sharedPreferences.edit().clear().apply();
                accessToken = null;
                currentUserEmail.postValue(null);
                isLoggedIn.postValue(false);
                isLoading.postValue(false);
            }
        }).start();
    }
}