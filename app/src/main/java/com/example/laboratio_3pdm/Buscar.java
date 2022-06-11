package com.example.laboratio_3pdm;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.laboratio_3pdm.modelo.Modelo;
import com.example.laboratio_3pdm.modelo.Service;
import com.example.laboratio_3pdm.serviceUtils.apiUtils;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Buscar extends AppCompatActivity {
    //Variables a utilizar
    public EditText palabra;
    public TextView ejemplo;
    public String Ejemplo, Audio;
    public Service servicioImplementado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);
        //vinculacion de variables
        palabra = findViewById(R.id.TxtPalabra);
        ejemplo = findViewById(R.id.TxtEjemplo);

    }

    //accion del boton buscar
    public void ClickBuscar(View v){
        //se almacela lo digitado
        String Palabra = palabra.getText().toString();
        //se obtienen la api
        servicioImplementado = apiUtils.getService();
        //crea un objeto y se envia dato
        Call<List<Modelo>> call = servicioImplementado.find(Palabra);

        call.enqueue(new Callback<List<Modelo>>() {
            @Override
            public void onResponse(Call<List<Modelo>> call, Response<List<Modelo>> response) {
                //se reinicia el valor de las variables
                Ejemplo = null;
                Audio = null;

                //contadores para realizar el recorrido
                int contA = 0;
                int contD = 0;

                //compara si se ha obtenido datos
                if (response.isSuccessful()) {

                    //ciclo while para que recorra hasta que no este vacio
                    while (Audio == null || Audio == "null" || Audio == "") {

                        //ciclo foreach para recorrido de datos
                        for (Modelo itemsModelo : response.body()) {

                            //se asigna valor a la variable
                            Audio = String.valueOf(itemsModelo.phonetics.get(contA).audio);

                            //compara que la variable sea diferente a ciertos parametros
                            if (Audio != null && Audio != "null" && Audio != "") {

                                //al serlo manda mensaje a consola
                                Log.d("RESPUESTA >", String.valueOf(itemsModelo.phonetics.get(contA).audio));

                                //rompe el ciclo
                                break;

                            }
                            //aumenta el valor al contador del audio
                            contA += 1;
                        }

                    }

                    //ciclo while para que recorra hasta que no este vacio
                    while (Ejemplo == null || Ejemplo == "null" || Ejemplo == "") {

                        //contador para los ejemplos
                        int contE = 0;

                        //ciclo foreach para recorrido de datos
                        for (Modelo itemsModelo : response.body()) {

                            //se asigna valor a la variable
                            Ejemplo = String.valueOf(itemsModelo.meanings.get(contD).definitions.get(contE).example);

                            //compara que la variable sea diferente a ciertos parametros
                            if (Ejemplo != null && Ejemplo != "null" && Ejemplo != "") {

                                //al serlo manda mensaje a consola
                                Log.d("RESPUESTA >", String.valueOf(itemsModelo.meanings.get(contD).definitions.get(contE).example));

                                //asigna valor al texview
                                ejemplo.setText(Ejemplo);

                                //rompe el ciclo
                                break;

                            }
                            //aumenta el valor al contador del ejemplo
                            contE += 1;

                            //compara si el contador es igual al tamaño del arreglo
                            if (contE == response.body().size()) {

                                //al serlo rompe el ciclo
                                break;
                            }

                        }

                        //aumenta el valor al contador del definitions
                        contD += 1;
                    }

                }
                //al no obtener datos
                else{
                    //se asigna valor
                    ejemplo.setText("Palabra no encontrada");
                }

            }
            @Override
            public void onFailure(Call<List<Modelo>> call, Throwable t) {

                //mensaje a consola sobre un fallo
                Log.d("Fallo >", String.valueOf(t));
            }
        });

    }

    //accion del boton reproducir
    public void ClickReproducir(View v){

        //si audio llega vacio
        if (Audio == null){
            //se asigna valor
            ejemplo.setText("Audio no encontrada");
        }
        //de lo contrario puede reproducir
        else {

            //Objeto para utilizar los multimedia
            MediaPlayer mediaPlayer = new MediaPlayer();
            //asignacion de accion
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //try catch para la asignacion de audio
            try {
                //se asigna la direccion del audio
                mediaPlayer.setDataSource(Audio);
            } catch (IOException e) {
                //en caso de hacer algun error
                e.printStackTrace();
            }
            //
            try {
                //almacenamiento del buffer
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //se reproduce el audio
            mediaPlayer.start();
        }
    }

}