package com.example.factsphere.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.factsphere.model.Fact;
import com.example.factsphere.model.QuizQuestion;
import com.example.factsphere.repository.FactRepository;
import com.example.factsphere.repository.QuizRepository;

import java.util.ArrayList;
import java.util.List;

public class QuizViewModel extends AndroidViewModel {

    private final QuizRepository               quizRepository  = new QuizRepository();
    private final FactRepository               factRepository  = new FactRepository();
    private final MutableLiveData<QuizQuestion> currentQuestion = new MutableLiveData<>();
    private final MutableLiveData<Integer>     score           = new MutableLiveData<>(0);
    private final MutableLiveData<Integer>     questionIndex   = new MutableLiveData<>(0);
    private final MutableLiveData<Integer>     totalQuestions  = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean>     isQuizFinished  = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean>     isCorrect       = new MutableLiveData<>();
    private final MutableLiveData<String>      selectedAnswer  = new MutableLiveData<>();
    private final MutableLiveData<Boolean>     isLoading       = new MutableLiveData<>(false);
    private final MutableLiveData<String>      errorMessage    = new MutableLiveData<>();

    private List<QuizQuestion> questionList = new ArrayList<>();
    private List<Fact> collectedFacts       = new ArrayList<>();

    // Keyword yang akan diambil dari Wikipedia Indonesia
    private static final String[] QUIZ_KEYWORDS = {
            "Lubang_hitam", "DNA", "Tsunami", "Fotosintesis",
            "Gravitasi", "Gunung_berapi", "Gempa_bumi", "Tata_Surya",
            "Evolusi", "Atom"
    };

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
    public LiveData<Boolean>      getIsLoading()       { return isLoading; }
    public LiveData<String>       getErrorMessage()    { return errorMessage; }

    // Dipanggil dari Fragment untuk mulai load dan generate quiz
    public void loadAndStartQuiz() {
        if (!collectedFacts.isEmpty()) {
            // Kalau sudah pernah load, langsung startQuiz
            startQuiz(collectedFacts);
            return;
        }
        isLoading.setValue(true);
        collectedFacts.clear();
        fetchNextFact(0);
    }

    // Ambil artikel Wikipedia satu per satu secara rekursif
    private void fetchNextFact(int index) {
        if (index >= QUIZ_KEYWORDS.length) {
            // Semua keyword sudah diambil
            startQuiz(collectedFacts);
            isLoading.postValue(false);
            return;
        }

        String keyword = QUIZ_KEYWORDS[index];
        factRepository.getArticleDetail(keyword, new FactRepository.ArticleCallback() {
            @Override
            public void onSuccess(String shortFact, String longArticle,
                                  String imageUrl, String wikipediaTitle) {
                // Potong shortFact maksimal 150 karakter
                String short_ = shortFact != null && shortFact.length() > 150
                        ? shortFact.substring(0, 150) + "..."
                        : shortFact;

                // Buat Fact sesuai konstruktor Fact.java milik Faqih
                Fact fact = new Fact(
                        String.valueOf(index), // id
                        wikipediaTitle,         // title
                        short_,                 // shortFact
                        "Umum",                 // category
                        imageUrl != null ? imageUrl : "", // imageUrl
                        wikipediaTitle          // wikipediaTitle
                );

                collectedFacts.add(fact);
                fetchNextFact(index + 1);
            }

            @Override
            public void onFailure(String message) {
                // Lewati keyword yang gagal, lanjut ke berikutnya
                fetchNextFact(index + 1);
            }
        });
    }

    // Generate soal dari list fakta
    public void startQuiz(List<Fact> facts) {
        questionList = quizRepository.generateQuestions(facts);

        totalQuestions.postValue(questionList.size());
        score.postValue(0);
        questionIndex.postValue(0);
        isQuizFinished.postValue(false);
        isCorrect.postValue(null);
        selectedAnswer.postValue(null);

        if (!questionList.isEmpty()) {
            currentQuestion.postValue(questionList.get(0));
        } else {
            errorMessage.postValue("Soal tidak cukup, coba lagi");
        }
    }

    // Jawab soal
    public void answerQuestion(String answer) {
        QuizQuestion question = currentQuestion.getValue();
        Integer index         = questionIndex.getValue();
        Integer currentScore  = score.getValue();
        if (question == null || index == null || currentScore == null) return;

        selectedAnswer.setValue(answer);
        boolean correct = answer.equals(question.getCorrectAnswer());
        isCorrect.setValue(correct);
        if (correct) score.setValue(currentScore + 1);
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

    // Ulangi quiz
    public void restartQuiz() {
        startQuiz(collectedFacts);
    }

    public String getFinalScoreText() {
        Integer finalScore = score.getValue();
        Integer total      = totalQuestions.getValue();
        if (finalScore == null || total == null) return "0/0";
        return finalScore + "/" + total;
    }
}