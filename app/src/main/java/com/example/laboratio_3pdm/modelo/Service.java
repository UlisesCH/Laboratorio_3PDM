package com.example.laboratio_3pdm.modelo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Service {

    @GET("en/{palabra}")
    public Call<List<Modelo>> find(@Path("palabra") String palabra);

}
