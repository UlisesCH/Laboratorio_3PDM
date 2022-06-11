package com.example.laboratio_3pdm.serviceUtils;

import com.example.laboratio_3pdm.modelo.Service;

public class apiUtils {

    public static final String base = "https://api.dictionaryapi.dev/api/v2/entries/";
    public static Service getService(){
        return apiPalabra.getClient(base).create(Service.class);
    }

}
