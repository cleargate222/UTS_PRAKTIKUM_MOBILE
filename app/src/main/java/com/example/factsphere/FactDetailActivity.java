package com.example.factsphere;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.factsphere.model.Fact;
import com.example.factsphere.viewmodel.FavoriteViewModel;

public class FactDetailActivity extends AppCompatActivity {

    private Fact fact;
    private FavoriteViewModel favoriteViewModel;
    private ImageView ivSave;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fact_detail);

        // 1. Ambil data yang dikirim dari Home
        fact = (Fact) getIntent().getSerializableExtra("FACT_DATA");

        // 2. Inisialisasi UI
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvContent = findViewById(R.id.tv_long_article);
        ImageView ivImage = findViewById(R.id.iv_detail_image);
        ivSave = findViewById(R.id.iv_save);

        if (fact != null) {
            tvTitle.setText(fact.getTitle());
            tvContent.setText(fact.getShortFact()); // Nanti bisa diupdate dengan long article
            Glide.with(this).load(fact.getImageUrl()).into(ivImage);
        }

        // 3. Setup Favorite ViewModel
        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);

        // Logika klik simpan
        ivSave.setOnClickListener(v -> {
            // Contoh userId sementara (nanti ambil dari SessionManager/Auth)
            String userId = "user123";
            String token = "your_supabase_token";

            favoriteViewModel.toggleFavorite(userId, token, fact);
            Toast.makeText(this, "Berhasil diperbarui", Toast.LENGTH_SHORT).show();
            updateFavoriteIcon();
        });
    }

    private void updateFavoriteIcon() {
        // Logika ganti icon simpan/aktif
    }
}