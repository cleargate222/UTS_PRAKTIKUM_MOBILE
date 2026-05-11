package com.example.factsphere;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private FactAdapter factAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_feed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // TODO: Initialize adapter for recycler_feed

        // ==================== TAMBAHAN ====================

        // Setup data dummy sementara
        List<Fact> factList = getDummyFacts();

        // Inisialisasi Adapter
        factAdapter = new FactAdapter(requireContext(), factList);
        recyclerView.setAdapter(factAdapter);

        return view;
    }

    // Data dummy (nanti akan diganti dengan data dari API oleh Faqih/Galank)
    private List<Fact> getDummyFacts() {
        List<Fact> facts = new ArrayList<>();

        facts.add(new Fact("Honey Bee Threat",
                "Lebih banyak orang yang mati karena lebah dan anjing daripada ular dan laba-laba.",
                "Science", null, null));

        facts.add(new Fact("Human DNA",
                "Jika DNA manusia direntangkan, panjangnya bisa mencapai 2 meter per sel.",
                "Biology", null, null));

        facts.add(new Fact("Octopus Intelligence",
                "Gurita memiliki 3 jantung dan 9 otak. Mereka juga bisa mengubah warna kulitnya untuk berkomunikasi.",
                "Animals", null, null));

        facts.add(new Fact("Space Fact",
                "Satu hari di Venus lebih lama dari satu tahun di Bumi.",
                "Space", null, null));

        return facts;
    }
}