package com.example.laboratio_3pdm.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Phonetic {

    @SerializedName("audio")
    @Expose
    public String audio;

    public Phonetic(String audio) {
        this.audio = audio;
    }

}
