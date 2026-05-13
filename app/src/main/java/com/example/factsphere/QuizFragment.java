package com.example.factsphere;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.factsphere.model.QuizQuestion;
import com.example.factsphere.viewmodel.QuizViewModel;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class QuizFragment extends Fragment {

    private QuizViewModel quizViewModel;

    private MaterialCardView option1, option2, option3, option4;
    private TextView tvOption1, tvOption2, tvOption3, tvOption4;
    private TextView tvQuestion, tvProgress;

    private String answer1, answer2, answer3, answer4;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_quiz, container, false);

        option1   = view.findViewById(R.id.option_1);
        option2   = view.findViewById(R.id.option_2);
        option3   = view.findViewById(R.id.option_3);
        option4   = view.findViewById(R.id.option_4);
        tvOption1 = view.findViewById(R.id.tv_option1);
        tvOption2 = view.findViewById(R.id.tv_option2);
        tvOption3 = view.findViewById(R.id.tv_option3);
        tvOption4 = view.findViewById(R.id.tv_option4);
        tvQuestion = view.findViewById(R.id.tv_question);
        tvProgress = view.findViewById(R.id.tv_progress);

        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        observeViewModel();
        setOptionClickListeners();

        // Load dari Wikipedia dan mulai quiz
        quizViewModel.loadAndStartQuiz();

        return view;
    }

    private void observeViewModel() {

        // Loading → sembunyikan option dulu
        quizViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading == null) return;
            if (isLoading) {
                tvQuestion.setText("Memuat soal dari Wikipedia...");
                tvProgress.setText("...");
                option1.setVisibility(View.INVISIBLE);
                option2.setVisibility(View.INVISIBLE);
                option3.setVisibility(View.INVISIBLE);
                option4.setVisibility(View.INVISIBLE);
            } else {
                option1.setVisibility(View.VISIBLE);
                option2.setVisibility(View.VISIBLE);
                option3.setVisibility(View.VISIBLE);
                option4.setVisibility(View.VISIBLE);
            }
        });

        // Soal berubah
        quizViewModel.getCurrentQuestion().observe(getViewLifecycleOwner(), question -> {
            if (question != null) tampilkanSoal(question);
        });

        // Progress nomor soal
        quizViewModel.getQuestionIndex().observe(getViewLifecycleOwner(), index -> {
            Integer total = quizViewModel.getTotalQuestions().getValue();
            if (index != null && total != null) {
                tvProgress.setText((index + 1) + " / " + total);
            }
        });

        // Hasil jawaban
        quizViewModel.getIsCorrect().observe(getViewLifecycleOwner(), isCorrect -> {
            if (isCorrect == null) return;
            Toast.makeText(getContext(),
                    isCorrect ? "✅ Benar!" : "❌ Salah!",
                    Toast.LENGTH_SHORT).show();

            option1.postDelayed(() -> {
                resetOptions();
                quizViewModel.nextQuestion();
            }, 1000);
        });

        // Error
        quizViewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null)
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });

        // Quiz selesai
        quizViewModel.getIsQuizFinished().observe(getViewLifecycleOwner(), isFinished -> {
            if (isFinished == null || !isFinished) return;
            tampilkanHasilAkhir();
        });
    }

    private void tampilkanSoal(QuizQuestion question) {
        tvQuestion.setText(question.getQuestion());

        List<String> allAnswers = question.getAllAnswers();
        answer1 = allAnswers.get(0);
        answer2 = allAnswers.get(1);
        answer3 = allAnswers.get(2);
        answer4 = allAnswers.get(3);

        tvOption1.setText(answer1);
        tvOption2.setText(answer2);
        tvOption3.setText(answer3);
        tvOption4.setText(answer4);
    }

    private void setOptionClickListeners() {
        option1.setOnClickListener(v -> pilihJawaban(option1, answer1));
        option2.setOnClickListener(v -> pilihJawaban(option2, answer2));
        option3.setOnClickListener(v -> pilihJawaban(option3, answer3));
        option4.setOnClickListener(v -> pilihJawaban(option4, answer4));
    }

    private void pilihJawaban(MaterialCardView selected, String answer) {
        resetOptions();
        selected.setStrokeColor(getResources().getColor(R.color.primary));
        quizViewModel.answerQuestion(answer);
    }

    private void tampilkanHasilAkhir() {
        option1.setVisibility(View.GONE);
        option2.setVisibility(View.GONE);
        option3.setVisibility(View.GONE);
        option4.setVisibility(View.GONE);
        tvQuestion.setText("Quiz Selesai!\nSkor kamu: "
                + quizViewModel.getFinalScoreText());
        tvProgress.setText("Selesai!");
    }

    private void resetOptions() {
        option1.setStrokeColor(0);
        option2.setStrokeColor(0);
        option3.setStrokeColor(0);
        option4.setStrokeColor(0);
    }
}