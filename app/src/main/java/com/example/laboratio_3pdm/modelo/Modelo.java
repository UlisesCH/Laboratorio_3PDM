package com.example.laboratio_3pdm.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Modelo {

    @SerializedName("word")
    @Expose
    public String word;
    @SerializedName("phonetics")
    @Expose
    public List<Phonetic> phonetics = null;
    @SerializedName("meanings")
    @Expose
    public List<Meaning> meanings = null;

    public Modelo(String word, List<Phonetic> phonetics, List<Meaning> meanings) {
        this.word = word;
        this.phonetics = phonetics;
        this.meanings = meanings;
    }

}
