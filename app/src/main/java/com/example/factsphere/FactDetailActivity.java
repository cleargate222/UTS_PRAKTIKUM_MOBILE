package com.example.factsphere;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.factsphere.model.Fact;
import com.example.factsphere.viewmodel.AuthViewModel;
import com.example.factsphere.viewmodel.FavoriteViewModel;
import com.google.android.material.button.MaterialButton;

public class FactDetailActivity extends AppCompatActivity {
    private ImageView ivSave;
    private FavoriteViewModel favoriteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fact_detail);

        // 1. Inisialisasi View
        ImageView ivDetailImage = findViewById(R.id.iv_detail_image);
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvLongArticle = findViewById(R.id.tv_long_article);
        MaterialButton btnSource = findViewById(R.id.btn_visit_source);
        ivSave = findViewById(R.id.iv_save);

        // 2. Inisialisasi ViewModel
        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);

        // 3. Ambil data dari Intent
        Fact fact = getIntent().getParcelableExtra("EXTRA_FACT");
        String token = getIntent().getStringExtra("EXTRA_TOKEN");
        String userEmail = getIntent().getStringExtra("EXTRA_EMAIL");

        Log.d("FAVORITE_DEBUG", "DETAIL -> Menerima Token: " + token);
        Log.d("FAVORITE_DEBUG", "DETAIL -> Menerima Email: " + userEmail);

        if (fact != null) {
            tvTitle.setText(fact.getTitle());
            tvLongArticle.setText(fact.getShortFact());
            Glide.with(this).load(fact.getImageUrl()).into(ivDetailImage);

            // Cek kondisi Login
            if (token != null && userEmail != null && !token.isEmpty()) {
                // Muat data favorit
                favoriteViewModel.loadFavorites(userEmail, token);

                // Update ikon secara otomatis
                favoriteViewModel.getFavorites().observe(this, list -> {
                    if (favoriteViewModel.isFavorite(fact.getId())) {
                        ivSave.setColorFilter(getResources().getColor(R.color.primary));
                    } else {
                        ivSave.setColorFilter(null);
                    }
                });

                ivSave.setOnClickListener(v -> {
                    favoriteViewModel.toggleFavorite(userEmail, token, fact);
                    // Feedback langsung
                    boolean isCurrentlyFav = favoriteViewModel.isFavorite(fact.getId());
                    Toast.makeText(this, isCurrentlyFav ? "Dihapus" : "Disimpan", Toast.LENGTH_SHORT).show();
                });
            } else {
                // Jika Token Null, arahkan user untuk login
                ivSave.setOnClickListener(v -> {
                    Log.e("FAVORITE_DEBUG", "Klik ditolak karena Token NULL");
                    Toast.makeText(this, "Sesi berakhir, silakan login kembali", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }
}