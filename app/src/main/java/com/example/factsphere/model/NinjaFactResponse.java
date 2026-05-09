package com.example.factsphere.model;

import com.google.gson.annotations.SerializedName;

public class NinjaFactResponse {

    @SerializedName("fact")
    private String fact;

    public String getFact() { return fact; }
}