package com.example.factsphere;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class FactsByCategoryFragment extends Fragment {

    private RecyclerView recyclerFacts;
    private FactAdapter factAdapter;
    private MaterialToolbar toolbar;

    private String categoryName;

    public static FactsByCategoryFragment newInstance(String categoryName) {
        FactsByCategoryFragment fragment = new FactsByCategoryFragment();
        Bundle args = new Bundle();
        args.putString("category_name", categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Updated to use the correct layout name and added missing imports for List and ArrayList
        View view = inflater.inflate(R.layout.activity_facts_by_category, container, false);

        toolbar = view.findViewById(R.id.toolbar_category);
        recyclerFacts = view.findViewById(R.id.recycler_facts);

        // Ambil nama kategori dari argument
        if (getArguments() != null) {
            categoryName = getArguments().getString("category_name", "Category");
            toolbar.setTitle(categoryName);
        }

        // Setup RecyclerView
        recyclerFacts.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load data fakta sesuai kategori
        List<Fact> facts = getFactsByCategory(categoryName);

        factAdapter = new FactAdapter(requireContext(), facts);
        recyclerFacts.setAdapter(factAdapter);

        // Tombol Back di Toolbar
        toolbar.setNavigationOnClickListener(v -> {
            // Kembali ke DiscoverFragment
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    // Dummy data fakta berdasarkan kategori
    private List<Fact> getFactsByCategory(String categoryName) {
        List<Fact> facts = new ArrayList<>();

        facts.add(new Fact(categoryName + " Fact 1",
                "Ini adalah fakta menarik nomor satu tentang " + categoryName + ".",
                categoryName, null, null));

        facts.add(new Fact(categoryName + " Fact 2",
                "Fakta kedua yang sangat edukatif dan mengejutkan tentang " + categoryName + ".",
                categoryName, null, null));

        facts.add(new Fact(categoryName + " Fact 3",
                "Fakta ketiga yang bisa kamu bagikan ke teman-temanmu.",
                categoryName, null, null));

        return facts;
    }
}
