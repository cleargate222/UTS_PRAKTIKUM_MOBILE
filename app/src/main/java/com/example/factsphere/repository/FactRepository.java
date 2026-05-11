package com.example.factsphere.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.factsphere.model.Fact;

import java.util.ArrayList;
import java.util.List;

public class FactRepository {

    public LiveData<List<Fact>> getAllFacts() {
        MutableLiveData<List<Fact>> data = new MutableLiveData<>();

        // Dummy data, nanti diganti Retrofit dari Faqih
        List<Fact> dummyList = new ArrayList<>();
        dummyList.add(new Fact("1", "Black Hole",
                "Black hole adalah daerah dengan gravitasi sangat kuat.",
                "Penjelasan panjang tentang black hole...",
                "Astronomi", ""));
        dummyList.add(new Fact("2", "DNA",
                "DNA membawa informasi genetik semua makhluk hidup.",
                "Penjelasan panjang tentang DNA...",
                "Biologi", ""));
        dummyList.add(new Fact("3", "Tsunami",
                "Tsunami terjadi akibat gempa di bawah laut.",
                "Penjelasan panjang tentang tsunami...",
                "Geografi", ""));
        dummyList.add(new Fact("4", "Fotosintesis",
                "Fotosintesis adalah proses tumbuhan membuat makanan dari cahaya.",
                "Penjelasan panjang tentang fotosintesis...",
                "Biologi", ""));
        dummyList.add(new Fact("5", "Gravitasi",
                "Gravitasi adalah gaya tarik antara dua benda bermassa.",
                "Penjelasan panjang tentang gravitasi...",
                "Fisika", ""));

        data.setValue(dummyList);
        return data;
    }
}