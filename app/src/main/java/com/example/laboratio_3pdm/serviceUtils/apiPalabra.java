package com.example.laboratio_3pdm.serviceUtils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class apiPalabra {

    private static Retrofit retrofit = null;

    static Retrofit getClient(String urlBase){
        if (retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(urlBase)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }

}
