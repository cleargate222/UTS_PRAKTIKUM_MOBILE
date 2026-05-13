package com.example.factsphere;

import android.util.Log;
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
import com.example.factsphere.viewmodel.SearchViewModel;

import java.util.ArrayList;
import java.util.List;

public class FactAdapter extends RecyclerView.Adapter<FactAdapter.FactViewHolder> {

    private FactAdapter adapter;
    private SearchViewModel viewModel;

    private final List<Fact> factList = new ArrayList<>();
    private OnItemClickCallback onItemClickCallback;

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public interface OnItemClickCallback {
        void onItemClicked(Fact data);
    }

    public void setData(List<Fact> newData) {
        this.factList.clear();
        if (newData != null) {
            this.factList.addAll(newData);
            Log.d("DEBUG_APP", "Jumlah data masuk ke Adapter: " + newData.size());
        }
        notifyDataSetChanged();
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

        Glide.with(holder.itemView.getContext())
                .load(fact.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .fallback(R.drawable.ic_launcher_background) // Tambahkan ini
                .into(holder.ivFactImage);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickCallback != null) {
                onItemClickCallback.onItemClicked(fact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return factList.size();
    }

    static class FactViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFactImage;
        TextView tvCategory, tvTitle, tvShortFact;

        public FactViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFactImage = itemView.findViewById(R.id.iv_fact_image);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvShortFact = itemView.findViewById(R.id.tv_short_fact);
        }
    }
}