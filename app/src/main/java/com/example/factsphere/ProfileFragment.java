package com.example.factsphere;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.factsphere.viewmodel.AuthViewModel;

public class ProfileFragment extends Fragment {

    private AuthViewModel authViewModel;
    private TextView tvUserName, tvUserEmail;
    private SwitchCompat switchDarkMode;
    private View btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Menggunakan layout activity_profile yang sudah kamu sediakan
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        // 1. Inisialisasi ViewModel (Gunakan requireActivity() agar sinkron dengan data Login)
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // 2. Inisialisasi View sesuai ID di layout XML
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Observasi ulang di sini untuk memastikan UI update saat fragment aktif
        authViewModel.getCurrentUserEmail().observe(getViewLifecycleOwner(), email -> {
            if (email != null && !email.isEmpty()) {
                tvUserEmail.setText(email);
                String name = email.split("@")[0];
                tvUserName.setText(name);
            }
        });

        // 4. Logika Dark Mode
        if (switchDarkMode != null) {
            // Cek status dark mode saat ini agar switch sinkron
            boolean isDark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
            switchDarkMode.setChecked(isDark);

            switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            });
        }

        // 5. Logika Logout
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                authViewModel.logout();
                Toast.makeText(getContext(), "Logout berhasil", Toast.LENGTH_SHORT).show();
                // 1. Panggil fungsi logout di ViewModel
                authViewModel.logout();
            });
        }

        // 2. Observasi status login. Jika berubah jadi false, arahkan ke Login
        authViewModel.getIsLoggedIn().observe(getViewLifecycleOwner(), isLoggedIn -> {
            if (!isLoggedIn) {
                Toast.makeText(getContext(), "Logout berhasil", Toast.LENGTH_SHORT).show();

                // Redirect ke LoginActivity
                Intent intent = new Intent(requireActivity(), LoginActivity.class);

                // Flag ini penting untuk menghapus semua tumpukan Activity sebelumnya
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                requireActivity().finish();
            }
        });

        return view;
    }
}