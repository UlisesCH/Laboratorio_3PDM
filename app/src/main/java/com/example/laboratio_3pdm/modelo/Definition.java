package com.example.laboratio_3pdm.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Definition {

    @SerializedName("definition")
    @Expose
    public String definition;
    @SerializedName("example")
    @Expose
    public String example;

    public Definition(String definition, String example) {
        this.definition = definition;
        this.example = example;
    }

}
