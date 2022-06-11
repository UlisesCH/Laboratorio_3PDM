package com.example.laboratio_3pdm.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Meaning {

    @SerializedName("definitions")
    @Expose
    public List<Definition> definitions = null;

    public Meaning(List<Definition> definitions) {
        this.definitions = definitions;
    }

}
