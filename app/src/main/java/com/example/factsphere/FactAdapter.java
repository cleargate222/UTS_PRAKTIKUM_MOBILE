package com.example.factsphere;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class FactAdapter extends RecyclerView.Adapter<FactAdapter.FactViewHolder> {

    private Context context;
    private List<Fact> factList;

    public FactAdapter(Context context, List<Fact> factList) {
        this.context = context;
        this.factList = factList;
    }

    @NonNull
    @Override
    public FactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fact_feed, parent, false);
        return new FactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FactViewHolder holder, int position) {
        Fact fact = factList.get(position);

        holder.tvTitle.setText(fact.getTitle());
        holder.tvShortFact.setText(fact.getShortFact());
        holder.tvCategory.setText(fact.getCategory());

        // Load image using Glide if URL is available
        if (fact.getImageUrl() != null && !fact.getImageUrl().isEmpty()) {
            Glide.with(context).load(fact.getImageUrl()).into(holder.ivFactImage);
        } else {
            // Set a default placeholder if no image URL is provided
            holder.ivFactImage.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.itemView.setOnClickListener(v -> {
            // TODO: Implement navigation to detail screen
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
