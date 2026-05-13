package com.example.factsphere;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import androidx.lifecycle.ViewModelProvider;
import com.example.factsphere.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullname, etEmail, etPassword;
    private MaterialButton btnRegister;
    private TextView tvLogin;
    // ... variabel view yang sudah ada
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        etFullname = findViewById(R.id.et_fullname);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);

        // Observasi hasil register
        authViewModel.getIsLoggedIn().observe(this, isRegistered -> {
            if (isRegistered) {
                Toast.makeText(this, "Registrasi Berhasil! Cek Email Anda Untuk Konfirmasi", Toast.LENGTH_SHORT).show();
                // Opsional: Langsung ke MainActivity atau kembali ke Login
                finish();
            }
        });

        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        btnRegister.setOnClickListener(v -> {
            String fullname = etFullname.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (fullname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show();
            } else {
                // Panggil method register dari ViewModel
                authViewModel.register(email, password);
            }
        });

        tvLogin.setOnClickListener(v -> {
            finish(); // Kembali ke Login Screen
        });
    }
}