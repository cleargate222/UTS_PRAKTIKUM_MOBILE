package com.example.factsphere;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.factsphere.viewmodel.AuthViewModel;
import com.example.factsphere.viewmodel.FactViewModel;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerFeed;
    private FactAdapter adapter; // Deklarasikan di sini
    private FactViewModel viewModel;
    private AuthViewModel authViewModel; // TAMBAHKAN INI

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Pakai activity_home.xml dulu (karena sudah ada RecyclerView-nya)
        return inflater.inflate(R.layout.activity_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerFeed = view.findViewById(R.id.recycler_feed);
        recyclerFeed.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FactAdapter();
        recyclerFeed.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(FactViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        viewModel.getFactList().observe(getViewLifecycleOwner(), facts -> {
            if (facts != null) {
                Log.d("HomeFragment", "Observer menerima " + facts.size() + " data");
                adapter.setData(facts);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Log.e("HomeFragment", "❌ Error: " + error);
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });

        Log.d("HomeFragment", "onViewCreated: Siap load data");

        // === PENTING: Cegah pemanggilan berulang ===
        if (savedInstanceState == null) {
            viewModel.loadMultipleFacts("Indonesia"); // Sekarang akan muncul 15 data berturut-turut!
        }

        // Alur Klik: Pindah ke Detail Activity
        adapter.setOnItemClickCallback(fact -> {
            Intent intent = new Intent(getActivity(), FactDetailActivity.class);

            // Ambil Token dan Email dari ViewModel
            String token = authViewModel.getAccessToken();
            String email = null;
            if (authViewModel.getCurrentUserEmail() != null && authViewModel.getCurrentUserEmail().getValue() != null) {
                email = authViewModel.getCurrentUserEmail().getValue();
            }

            Log.d("FAVORITE_DEBUG", "HOME -> Mengirim Token: " + token);
            Log.d("FAVORITE_DEBUG", "HOME -> Mengirim Email: " + email);

            intent.putExtra("EXTRA_FACT", fact);
            intent.putExtra("EXTRA_TOKEN", token);
            intent.putExtra("EXTRA_EMAIL", email);

            startActivity(intent);
        });
    }
}
