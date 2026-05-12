package com.example.factsphere;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;

public class QuizFragment extends Fragment {

    private MaterialCardView option1, option2, option3, option4;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_quiz, container, false);

        // Inisialisasi
        option1 = view.findViewById(R.id.option_1);
        option2 = view.findViewById(R.id.option_2);
        option3 = view.findViewById(R.id.option_3);
        option4 = view.findViewById(R.id.option_4);

        setOptionClickListeners();

        return view;
    }

    private void setOptionClickListeners() {
        View.OnClickListener listener = v -> {
            resetOptions();
            MaterialCardView selected = (MaterialCardView) v;
            selected.setStrokeColor(getResources().getColor(R.color.primary));

            Toast.makeText(getContext(), "Jawaban dipilih!", Toast.LENGTH_SHORT).show();
        };

        option1.setOnClickListener(listener);
        option2.setOnClickListener(listener);
        option3.setOnClickListener(listener);
        option4.setOnClickListener(listener);
    }

    private void resetOptions() {
        option1.setStrokeColor(0);
        option2.setStrokeColor(0);
        option3.setStrokeColor(0);
        option4.setStrokeColor(0);
    }
}