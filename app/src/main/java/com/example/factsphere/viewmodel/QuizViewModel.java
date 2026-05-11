package com.example.factsphere.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.factsphere.model.Fact;
import com.example.factsphere.model.QuizQuestion;
import com.example.factsphere.repository.QuizRepository;

import java.util.ArrayList;
import java.util.List;

public class QuizViewModel extends AndroidViewModel {

    private final QuizRepository            repository      = new QuizRepository();
    private final MutableLiveData<QuizQuestion> currentQuestion = new MutableLiveData<>();
    private final MutableLiveData<Integer>  score           = new MutableLiveData<>(0);
    private final MutableLiveData<Integer>  questionIndex   = new MutableLiveData<>(0);
    private final MutableLiveData<Integer>  totalQuestions  = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean>  isQuizFinished  = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean>  isCorrect       = new MutableLiveData<>();
    private final MutableLiveData<String>   selectedAnswer  = new MutableLiveData<>();

    private List<QuizQuestion> questionList = new ArrayList<>();

    public QuizViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<QuizQuestion> getCurrentQuestion() { return currentQuestion; }
    public LiveData<Integer>      getScore()           { return score; }
    public LiveData<Integer>      getQuestionIndex()   { return questionIndex; }
    public LiveData<Integer>      getTotalQuestions()  { return totalQuestions; }
    public LiveData<Boolean>      getIsQuizFinished()  { return isQuizFinished; }
    public LiveData<Boolean>      getIsCorrect()       { return isCorrect; }
    public LiveData<String>       getSelectedAnswer()  { return selectedAnswer; }

    // Generate soal dari semua fakta
    public void startQuiz(List<Fact> facts) {
        questionList = repository.generateQuestions(facts);
        totalQuestions.setValue(questionList.size());
        score.setValue(0);
        questionIndex.setValue(0);
        isQuizFinished.setValue(false);
        isCorrect.setValue(null);
        selectedAnswer.setValue(null);

        if (!questionList.isEmpty()) {
            currentQuestion.setValue(questionList.get(0));
        }
    }

    // Generate soal berdasarkan kategori tertentu
    public void startQuizByCategory(List<Fact> facts, String category) {
        List<Fact> filtered = new ArrayList<>();
        for (Fact f : facts) {
            if (f.getCategory().equalsIgnoreCase(category)) {
                filtered.add(f);
            }
        }
        startQuiz(filtered);
    }

    // Jawab soal
    public void answerQuestion(String answer) {
        QuizQuestion question = currentQuestion.getValue();
        Integer index         = questionIndex.getValue();
        Integer currentScore  = score.getValue();

        if (question == null || index == null || currentScore == null) return;

        selectedAnswer.setValue(answer);

        // Cek apakah jawaban benar
        boolean correct = answer.equals(question.getCorrectAnswer());
        isCorrect.setValue(correct);

        if (correct) {
            score.setValue(currentScore + 1);
        }
    }

    // Lanjut ke soal berikutnya
    public void nextQuestion() {
        Integer index = questionIndex.getValue();
        if (index == null) return;

        int next = index + 1;
        if (next >= questionList.size()) {
            isQuizFinished.setValue(true);
        } else {
            questionIndex.setValue(next);
            currentQuestion.setValue(questionList.get(next));
            isCorrect.setValue(null);
            selectedAnswer.setValue(null);
        }
    }

    // Ulangi quiz dari awal
    public void restartQuiz() {
        score.setValue(0);
        questionIndex.setValue(0);
        isQuizFinished.setValue(false);
        isCorrect.setValue(null);
        selectedAnswer.setValue(null);

        if (!questionList.isEmpty()) {
            currentQuestion.setValue(questionList.get(0));
        }
    }

    // Ambil hasil akhir sebagai string
    public String getFinalScoreText() {
        Integer finalScore = score.getValue();
        Integer total      = totalQuestions.getValue();
        if (finalScore == null || total == null) return "0/0";
        return finalScore + "/" + total;
    }
}