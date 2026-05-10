package com.example.factsphere.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.factsphere.model.Fact;
import com.example.factsphere.model.QuizQuestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizViewModel extends AndroidViewModel {

    private final MutableLiveData<QuizQuestion> currentQuestion  = new MutableLiveData<>();
    private final MutableLiveData<Integer>      score            = new MutableLiveData<>(0);
    private final MutableLiveData<Integer>      questionIndex    = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean>      isQuizFinished   = new MutableLiveData<>(false);

    private final List<QuizQuestion> questionList = new ArrayList<>();
    private static final int TOTAL_QUESTIONS = 10;

    public QuizViewModel(@NonNull Application application) { super(application); }

    public LiveData<QuizQuestion> getCurrentQuestion() { return currentQuestion; }
    public LiveData<Integer>      getScore()           { return score; }
    public LiveData<Integer>      getQuestionIndex()   { return questionIndex; }
    public LiveData<Boolean>      getIsQuizFinished()  { return isQuizFinished; }

    public void generateQuiz(List<Fact> facts) {
        questionList.clear();
        List<Fact> shuffled = new ArrayList<>(facts);
        Collections.shuffle(shuffled);

        for (int i = 0; i < Math.min(TOTAL_QUESTIONS, shuffled.size()); i++) {
            Fact correct = shuffled.get(i);
            List<String> wrongAnswers = new ArrayList<>();
            for (Fact f : shuffled) {
                if (!f.getId().equals(correct.getId()) && wrongAnswers.size() < 3) {
                    wrongAnswers.add(f.getShortFact());
                }
            }
            questionList.add(new QuizQuestion(
                    "Apa yang dimaksud dengan " + correct.getTitle() + "?",
                    correct.getShortFact(),
                    wrongAnswers,
                    correct.getCategory()
            ));
        }

        score.setValue(0);
        questionIndex.setValue(0);
        isQuizFinished.setValue(false);
        if (!questionList.isEmpty()) {
            currentQuestion.setValue(questionList.get(0));
        }
    }

    public void answerQuestion(String answer) {
        Integer index        = questionIndex.getValue();
        Integer currentScore = score.getValue();
        QuizQuestion q       = currentQuestion.getValue();
        if (q == null || index == null || currentScore == null) return;

        if (answer.equals(q.getCorrectAnswer())) {
            score.setValue(currentScore + 1);
        }

        int next = index + 1;
        if (next >= questionList.size()) {
            isQuizFinished.setValue(true);
        } else {
            questionIndex.setValue(next);
            currentQuestion.setValue(questionList.get(next));
        }
    }
}