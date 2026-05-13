package com.example.factsphere;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.factsphere.model.Fact;
import com.example.factsphere.viewmodel.FavoriteViewModel;
import com.google.android.material.button.MaterialButton;

public class FactDetailActivity extends AppCompatActivity {

    private ImageView ivSave;
    private FavoriteViewModel favoriteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fact_detail);

        // Inisialisasi View
        ImageView ivDetailImage = findViewById(R.id.iv_detail_image);
        TextView  tvTitle       = findViewById(R.id.tv_title);
        TextView  tvLongArticle = findViewById(R.id.tv_long_article);
        ivSave                  = findViewById(R.id.iv_save);

        // Inisialisasi ViewModel
        favoriteViewModel = new ViewModelProvider(this)
                .get(FavoriteViewModel.class);

        // Ambil data dari Intent
        Fact   fact      = getIntent().getParcelableExtra("EXTRA_FACT");
        String token     = getIntent().getStringExtra("EXTRA_TOKEN");
        String userEmail = getIntent().getStringExtra("EXTRA_EMAIL");

        Log.d("FAVORITE_DEBUG", "Token: " + token);
        Log.d("FAVORITE_DEBUG", "Email: " + userEmail);

        if (fact == null) return;

        // Tampilkan data
        tvTitle.setText(fact.getTitle());
        tvLongArticle.setText(fact.getShortFact());
        Glide.with(this).load(fact.getImageUrl()).into(ivDetailImage);

        if (token != null && userEmail != null && !token.isEmpty()) {

            // Load favorites dulu agar isFavorite() akurat
            favoriteViewModel.loadFavorites(userEmail, token);

            // Update icon saat favoriteList berubah
            favoriteViewModel.getFavorites().observe(this, list -> {
                updateBookmarkIcon(fact.getId());
            });

            // Observe toast dari ViewModel (lebih akurat)
            favoriteViewModel.getToastMessage().observe(this, msg -> {
                if (msg != null) {
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
            });

            // Klik bookmark
            ivSave.setOnClickListener(v -> {
                favoriteViewModel.toggleFavorite(userEmail, token, fact);
            });

        } else {
            ivSave.setOnClickListener(v -> {
                Toast.makeText(this,
                        "Sesi berakhir, silakan login kembali",
                        Toast.LENGTH_SHORT).show();
            });
        }
    }

    // Update icon bookmark sesuai status
    private void updateBookmarkIcon(String factId) {
        if (favoriteViewModel.isFavorite(factId)) {
            ivSave.setColorFilter(
                    getResources().getColor(R.color.primary));
        } else {
            ivSave.clearColorFilter();
        }
    }
}