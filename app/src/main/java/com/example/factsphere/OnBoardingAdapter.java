package com.example.factsphere;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OnBoardingAdapter extends RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder> {

    private final Context context;

    private final String[] titles = {
            "Selamat Datang di FactSphere",
            "Fakta Menarik Setiap Hari",
            "Uji Pengetahuanmu"
    };

    private final String[] descriptions = {
            "Temukan ribuan fakta menarik dari seluruh dunia dalam satu aplikasi.",
            "Baca short fact yang ringkas, atau klik Learn More untuk artikel lengkap.",
            "Tantang dirimu dengan mode Quiz yang seru dan edukatif."
    };

    private final int[] images = {
            R.drawable.ic_launcher_background, // Placeholder karena drawable onboard belum ada
            R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background
    };

    public OnBoardingAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public OnBoardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_onboarding, parent, false);
        return new OnBoardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnBoardingViewHolder holder, int position) {
        holder.tvTitle.setText(titles[position]);
        holder.tvDesc.setText(descriptions[position]);
        holder.ivImage.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    static class OnBoardingViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvDesc;

        public OnBoardingViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_onboard_image);
            tvTitle = itemView.findViewById(R.id.tv_onboard_title);
            tvDesc = itemView.findViewById(R.id.tv_onboard_desc);
        }
    }
}