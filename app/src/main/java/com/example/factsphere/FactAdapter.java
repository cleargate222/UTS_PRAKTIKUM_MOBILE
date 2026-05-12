package com.example.factsphere;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.factsphere.R;
import com.example.factsphere.model.Fact;

import java.util.ArrayList;
import java.util.List;

public class FactAdapter extends RecyclerView.Adapter<FactAdapter.FactViewHolder> {

    private List<Fact> factList = new ArrayList<>();

    // Fungsi untuk memperbarui data dari Fragment
    public void setData(List<Fact> newData) {
        this.factList = newData;
        notifyDataSetChanged(); // PENTING: Tanpa ini, layar tetap kosong
    }

    @NonNull
    @Override
    public FactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fact_feed, parent, false);
        return new FactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FactViewHolder holder, int position) {
        Fact fact = factList.get(position);
        holder.tvTitle.setText(fact.getTitle());
        holder.tvCategory.setText(fact.getCategory());
        holder.tvShortFact.setText(fact.getShortFact());

        // Jika kamu ingin menampilkan gambar menggunakan library Glide
        Glide.with(holder.itemView.getContext()).load(fact.getImageUrl()).into(holder.ivFactImage);
    }

    @Override
    public int getItemCount() {
        return factList != null ? factList.size() : 0;
    }

    // ViewHolder sebagai penampung komponen UI
    static class FactViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFactImage;
        TextView tvCategory, tvTitle, tvShortFact;

        public FactViewHolder(@NonNull View itemView) {
            super(itemView);
            // Sesuaikan dengan ID yang ada di item_category.xml
            ivFactImage = itemView.findViewById(R.id.iv_fact_image);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvShortFact = itemView.findViewById(R.id.tv_short_fact);
        }
    }
}