package com.example.factsphere;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.factsphere.viewmodel.AuthViewModel;
import com.example.factsphere.viewmodel.FavoriteViewModel;

public class FavoritesFragment extends Fragment {
    private RecyclerView recyclerView;
    private FactAdapter adapter;
    private FavoriteViewModel favoriteViewModel;
    private AuthViewModel authViewModel;
    private LinearLayout layoutEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Menggunakan layout activity_bookmark.xml sesuai kode Anda
        View view = inflater.inflate(R.layout.activity_bookmark, container, false);

        // 1. Inisialisasi View
        recyclerView = view.findViewById(R.id.recycler_favorites);
        layoutEmpty = view.findViewById(R.id.layout_empty_favorites);

        // 2. Setup RecyclerView & Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FactAdapter();
        recyclerView.setAdapter(adapter);

        // 3. Inisialisasi ViewModel
        // Gunakan requireActivity() agar AuthViewModel sinkron dengan session login di MainActivity
        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // PERBAIKAN: Kirim Token & Email juga saat klik dari halaman Favorite
        adapter.setOnItemClickCallback(fact -> {
            Intent intent = new Intent(getActivity(), FactDetailActivity.class);

            String token = authViewModel.getAccessToken();
            String email = (authViewModel.getCurrentUserEmail() != null) ?
                    authViewModel.getCurrentUserEmail().getValue() : null;

            intent.putExtra("EXTRA_FACT", fact);
            intent.putExtra("EXTRA_TOKEN", token);
            intent.putExtra("EXTRA_EMAIL", email);
            startActivity(intent);
        });

        // 5. Observasi LiveData dari FavoriteViewModel
        favoriteViewModel.getFavorites().observe(getViewLifecycleOwner(), facts -> {
            if (facts != null && !facts.isEmpty()) {
                // Jika ada data: Tampilkan List, Sembunyikan pesan kosong
                adapter.setData(facts);
                recyclerView.setVisibility(View.VISIBLE);
                layoutEmpty.setVisibility(View.GONE);
            } else {
                // Jika kosong: Sembunyikan List, Tampilkan pesan "Belum ada favorite"
                recyclerView.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    // Metode untuk memuat data dari Supabase via ViewModel
    private void loadData() {
        if (authViewModel != null) {
            String token = authViewModel.getAccessToken();
            String emailValue = null;

            if(authViewModel.getCurrentUserEmail() != null){
                emailValue = authViewModel.getCurrentUserEmail().getValue();
            }

            if (token != null && emailValue != null) {
                favoriteViewModel.loadFavorites(emailValue, token);
            }
        }
    }
    // .

    @Override
    public void onResume() {
        super.onResume();
        // Memanggil loadData setiap kali fragment aktif kembali (misal setelah dari DetailActivity)
        loadData();
    }
}