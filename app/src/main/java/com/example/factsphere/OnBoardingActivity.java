package com.example.factsphere;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.button.MaterialButton;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private DotsIndicator dotsIndicator;
    private MaterialButton btnNext;
    private TextView tvSkip;
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.view_pager);
        dotsIndicator = findViewById(R.id.dots_indicator);
        btnNext = findViewById(R.id.btn_next);
        tvSkip = findViewById(R.id.tv_skip);

        // Setup Data Onboarding
        List<OnBoardingItem> onBoardingItems = new ArrayList<>();
        onBoardingItems.add(new OnBoardingItem(
                R.drawable.home,
                "Selamat Datang di FactSphere",
                "Temukan berbagai fakta menarik dan unik dari seluruh penjuru dunia."
        ));
        onBoardingItems.add(new OnBoardingItem(
                R.drawable.quiz,
                "Asah Pengetahuanmu",
                "Uji wawasanmu dengan kuis-kuis yang menantang dan edukatif."
        ));
        onBoardingItems.add(new OnBoardingItem(
                R.drawable.discovery,
                "Eksplorasi Tanpa Batas",
                "Jelajahi kategori fakta yang beragam mulai dari sains hingga sejarah."
        ));

        // Setup Adapter Onboarding
        OnBoardingAdapter adapter = new OnBoardingAdapter(this, onBoardingItems);
        viewPager.setAdapter(adapter);

        // Hubungkan dengan Dots Indicator
        dotsIndicator.setViewPager2(viewPager);

        // Listener untuk ViewPager
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                if (position == 2) { // Slide terakhir
                    btnNext.setText("Mulai Sekarang");
                } else {
                    btnNext.setText("Selanjutnya");
                }
            }
        });

        // Tombol Next
        btnNext.setOnClickListener(v -> {
            if (currentPage < 2) {
                viewPager.setCurrentItem(currentPage + 1);
            } else {
                // Selesai Onboarding → ke Login
                Intent intent = new Intent(OnBoardingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Tombol Skip
        tvSkip.setOnClickListener(v -> {
            Intent intent = new Intent(OnBoardingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
