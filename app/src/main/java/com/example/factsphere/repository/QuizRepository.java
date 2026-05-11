package com.example.factsphere.repository;

import com.example.factsphere.model.Fact;
import com.example.factsphere.model.QuizQuestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizRepository {

    private static final int TOTAL_QUESTIONS  = 10;
    private static final int WRONG_ANSWER_COUNT = 3;

    public List<QuizQuestion> generateQuestions(List<Fact> facts) {
        List<QuizQuestion> questions = new ArrayList<>();

        // Minimal butuh 4 fakta (1 benar + 3 salah)
        if (facts == null || facts.size() < 4) return questions;

        // Acak urutan fakta
        List<Fact> shuffled = new ArrayList<>(facts);
        Collections.shuffle(shuffled);

        int limit = Math.min(TOTAL_QUESTIONS, shuffled.size());

        for (int i = 0; i < limit; i++) {
            Fact correct = shuffled.get(i);

            // Ambil 3 jawaban salah dari fakta lain
            List<String> wrongAnswers = new ArrayList<>();
            for (Fact f : shuffled) {
                if (!f.getId().equals(correct.getId()) &&
                        wrongAnswers.size() < WRONG_ANSWER_COUNT) {
                    wrongAnswers.add(f.getShortFact());
                }
            }

            // Pastikan sudah dapat 3 jawaban salah
            if (wrongAnswers.size() < WRONG_ANSWER_COUNT) continue;

            questions.add(new QuizQuestion(
                    "Apa yang dimaksud dengan " + correct.getTitle() + "?",
                    correct.getShortFact(),
                    wrongAnswers,
                    correct.getCategory()
            ));
        }

        return questions;
    }
}