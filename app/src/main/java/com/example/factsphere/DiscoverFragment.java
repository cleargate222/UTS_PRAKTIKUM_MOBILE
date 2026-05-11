package com.example.factsphere;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DiscoverFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_discover, container, false);

        RecyclerView recyclerCategory = view.findViewById(R.id.recycler_category);
        if (recyclerCategory != null) {
            recyclerCategory.setLayoutManager(new GridLayoutManager(getContext(), 2));
            // TODO: nanti tambahkan adapter kategori
        }

        return view;
    }
}