package com.example.factsphere.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizQuestion {
    private final String       question;
    private final String       correctAnswer;
    private final List<String> wrongAnswers;
    private final String       category;

    public QuizQuestion(String question, String correctAnswer,
                        List<String> wrongAnswers, String category) {
        this.question      = question;
        this.correctAnswer = correctAnswer;
        this.wrongAnswers  = wrongAnswers;
        this.category      = category;
    }

    // Semua jawaban diacak jadi satu list
    public List<String> getAllAnswers() {
        List<String> all = new ArrayList<>(wrongAnswers);
        all.add(correctAnswer);
        Collections.shuffle(all);
        return all;
    }

    public String       getQuestion()      { return question; }
    public String       getCorrectAnswer() { return correctAnswer; }
    public List<String> getWrongAnswers()  { return wrongAnswers; }
    public String       getCategory()      { return category; }
}