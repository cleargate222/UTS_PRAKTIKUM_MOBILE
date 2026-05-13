package com.example.factsphere;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.factsphere.model.Fact;
import com.example.factsphere.viewmodel.SearchViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class DiscoverFragment extends Fragment {

    private FactAdapter adapter;
    private SearchViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_discover, container, false);

        // 1. Inisialisasi UI
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        TextInputEditText etSearch = view.findViewById(R.id.et_search);

        // 2. Inisialisasi Adapter & ViewModel
        adapter = new FactAdapter();
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        // 3. Setup RecyclerView
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }

        // 4. Logika Pencarian & Perbaikan Tombol Enter
        if (etSearch != null) {
            etSearch.setOnEditorActionListener((v, actionId, event) -> {
                // Menangkap aksi Search dari keyboard (setelah update XML)
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    String query = etSearch.getText().toString().trim();

                    if (!query.isEmpty()) {
                        // Jalankan pencarian ke API Wikipedia
                        viewModel.search(query);

                        // FIX: Menutup keyboard setelah tekan cari
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        // Hilangkan fokus dari EditText
                        etSearch.clearFocus();
                    }
                    return true;
                }
                return false;
            });
        }

        // 5. Alur Klik: Pindah ke Detail Activity
        adapter.setOnItemClickCallback(fact -> {
            Intent intent = new Intent(getActivity(), FactDetailActivity.class);
            intent.putExtra("EXTRA_FACT", fact);
            startActivity(intent);
        });

        // 6. Update UI: Mengamati perubahan data secara real-time
        viewModel.getSearchResults().observe(getViewLifecycleOwner(), facts -> {
            if (facts != null && !facts.isEmpty()) {
                Log.d("UI_UPDATE", "Berhasil menampilkan " + facts.size() + " data.");
                adapter.setData(facts);
            }
        });

        return view;
    }
}