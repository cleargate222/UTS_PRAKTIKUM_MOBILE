package com.example.factsphere;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends Fragment {

    private RecyclerView recyclerCategory;
    private TextInputEditText etSearch;
    private CategoryAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_discover, container, false);

        recyclerCategory = view.findViewById(R.id.recycler_view);
        etSearch = view.findViewById(R.id.et_search);

        if (recyclerCategory != null) {
            recyclerCategory.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }

        // Setup Category Adapter
        setupCategoryData();

        // Search Functionality
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (categoryAdapter != null) {
                        categoryAdapter.getFilter().filter(s);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            // === TAMBAHAN: Mencegah Enter membuat baris baru ===
            etSearch.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER)) {

                    // Sembunyikan keyboard
                    InputMethodManager imm = (InputMethodManager) requireContext()
                            .getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                    }
                    return true; // Mencegah aksi default (enter ke bawah)
                }
                return false;
            });
        }

        return view;
    }

    private void setupCategoryData() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Animals", R.drawable.animal));
        categories.add(new Category("Science", R.drawable.science));
        categories.add(new Category("History", R.drawable.history));
        categories.add(new Category("Technology", R.drawable.teknologi));
        categories.add(new Category("Space", R.drawable.space));
        categories.add(new Category("Food", R.drawable.food));
        categories.add(new Category("Nature", R.drawable.nature));
        categories.add(new Category("Sports", R.drawable.sport));

        categoryAdapter = new CategoryAdapter(requireContext(), categories);

        // Listener Klik Kategori → Buka FactsByCategoryFragment
        categoryAdapter.setOnCategoryClickListener(category -> {
            FactsByCategoryFragment fragment = FactsByCategoryFragment.newInstance(category.getName());

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        if (recyclerCategory != null) {
            recyclerCategory.setAdapter(categoryAdapter);
        }
    }
}