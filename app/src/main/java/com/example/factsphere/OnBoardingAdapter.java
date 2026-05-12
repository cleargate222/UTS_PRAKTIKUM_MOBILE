package com.example.factsphere;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OnBoardingAdapter extends RecyclerView.Adapter<OnBoardingAdapter.OnboardingViewHolder> {

    private final Context context;
    private final List<OnBoardingItem> items;

    public OnBoardingAdapter(Context context, List<OnBoardingItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_onboarding, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        OnBoardingItem item = items.get(position);

        holder.ivImage.setImageResource(item.getImageRes());
        holder.tvTitle.setText(item.getTitle());
        holder.tvDesc.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvDesc;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_onboard_image);
            tvTitle = itemView.findViewById(R.id.tv_onboard_title);
            tvDesc = itemView.findViewById(R.id.tv_onboard_desc);
        }
    }
}