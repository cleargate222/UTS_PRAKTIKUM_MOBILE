package com.example.factsphere;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.factsphere.model.Fact;
import com.google.android.material.button.MaterialButton;

public class FactDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fact_detail);

        ImageView ivDetailImage = findViewById(R.id.iv_detail_image);
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvLongArticle = findViewById(R.id.tv_long_article);
        MaterialButton btnSource = findViewById(R.id.btn_visit_source);

        // Ambil data Parcelable dari Intent
        Fact fact = getIntent().getParcelableExtra("EXTRA_FACT");

        if (fact != null) {
            tvTitle.setText(fact.getTitle());
            tvLongArticle.setText(fact.getShortFact()); // Sementara menggunakan short fact sebagai isi

            Glide.with(this)
                    .load(fact.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(ivDetailImage);

            // Alur Visit Source
            btnSource.setOnClickListener(v -> {
                String url = "https://id.wikipedia.org/wiki/" + fact.getWikipediaTitle();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            });
        }
    }
}