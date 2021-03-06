package com.example.laboratio_3pdm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laboratio_3pdm.modelo.Modelo;
import com.example.laboratio_3pdm.modelo.Service;
import com.example.laboratio_3pdm.modelo.modeloHistorial;
import com.example.laboratio_3pdm.serviceUtils.apiUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Buscar extends AppCompatActivity {
    //Variables a utilizar
    public EditText palabra;
    public TextView ejemplo,tv_definicion;
    public String Ejemplo, Audio;
    public Service servicioImplementado;

    private FirebaseDatabase database;
    private DatabaseReference referenciaData;
    private FirebaseAuth auth;
    private String correoUsuario;
    //EN ESTA VARIABLE ALMACENAREMOS SOLO LA PARTE DEL CORREO ANTES DEL @
    private String correoAntesDeDominio = "";
    //PARA MOSTRAR EL TEXTO DE LA PRONUNCIACION CUANDO SE REPRODUZCA EL AUDIO
    private String textPronunciacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);
        //vinculacion de variables
        palabra = findViewById(R.id.TxtPalabra);
        ejemplo = findViewById(R.id.TxtEjemplo);
        tv_definicion = (TextView) findViewById(R.id.tvDefinicion);

        //inicializacion de variables para firebase
        database = FirebaseDatabase.getInstance();
        referenciaData = database.getReference();
        auth = FirebaseAuth.getInstance();

        //OBTENER EL EMAIL DEL USUARIO QUE INICIO SESION
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            correoUsuario = user.getEmail();
            //OBTENER EL CORREO DEL USUARIO CON SESION INICIADA TODO LO QUE ESTA ANTES DEL @
            for(int i = 0; i < correoUsuario.length();i++){
                if(correoUsuario.charAt(i) == '@'){
                    break;
                }else{
                    correoAntesDeDominio += correoUsuario.charAt(i);
                }

            }
        }else{
            correoUsuario = "";
        }
        //SI SE LE HA MANDADO UN DATO DESDE OTRA ACTIVIDAD HACE LA BUSQUEDA
        Log.d("PALABRA",""+getIntent().getStringExtra("PALABRA"));
        if(!(""+getIntent().getStringExtra("PALABRA")).equals("null")
        && !getIntent().getStringExtra("PALABRA").equals("")){
            palabra.setText(getIntent().getStringExtra("PALABRA"));
            Busqueda();
        }
    }


    //accion del boton buscar
    public void ClickBuscar(View v){
        Busqueda();
    }

    public void Busqueda(){

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

                //compara si se ha obtenido datos
                if (response.isSuccessful()) {
                    //PARA LOS DATOS QUE SE MANDARAN A FIREBASE PARA HISTORIAL
                    modeloHistorial h = new modeloHistorial();
                    h.palabra = palabra.getText().toString();

                    //variable para salir de un ciclo anidado en caso de encontrar algo
                    boolean encontro = false;
                    //PARA RECORRER TODA LA INFORMACION DE LA API
                    for (Modelo itemsModelo:response.body()) {

                        //OBTENER AUDIO
                        //RECORRER LA LISTA DE PHONETIC
                        for(int i = 0; i < itemsModelo.phonetics.size();i++ ){
                            //COMPROBAR QUE LA VARIABLE AUDIO DE PHONETIC ESTE OBTENIENDO ALGO
                            if(!(""+itemsModelo.phonetics.get(i).audio).equals("null")
                                    && !itemsModelo.phonetics.get(i).audio.isEmpty() &&
                                    !itemsModelo.phonetics.get(i).audio.equals("null")
                                    && itemsModelo.phonetics.get(i).audio != null ){

                                Audio = String.valueOf(itemsModelo.phonetics.get(i).audio);
                                break;

                            }
                        }

                        //PARA OBTENER EJEMPLO
                        encontro = false;
                        for(int i = 0; i < itemsModelo.meanings.size();i++){
                            for(int j = 0; j< itemsModelo.meanings.get(i).definitions.size();j++){
                                //COMPROBAR QUE LA VARIABLE EXAMPLE ESTE OBTENIENDO ALGO
                                if(!(""+itemsModelo.meanings.get(i).definitions.get(j).example).equals("null")
                                        && !itemsModelo.meanings.get(i).definitions.get(j).example.isEmpty() &&
                                        !itemsModelo.meanings.get(i).definitions.get(j).example.equals("null") &&
                                        itemsModelo.meanings.get(i).definitions.get(j).example != null){

                                    ejemplo.setText("Example: "+itemsModelo.meanings.get(i).definitions.get(j).example);
                                    h.ejemplo = itemsModelo.meanings.get(i).definitions.get(j).example;
                                    break;

                                }
                            }
                        }


                        //PARA OBTENER PRONUNCIACION
                        //RECORRER LA LISTA DE PHONETIC
                        for(int i = 0; i < itemsModelo.phonetics.size();i++ ){
                           //COMPROBAR QUE EL LA VARIABLE TEXT DE PHONETIC ESTE OBTENIENDO ALGO
                            if(!(""+itemsModelo.phonetics.get(i).text).equals("null")
                            && !itemsModelo.phonetics.get(i).text.isEmpty() &&
                                    !itemsModelo.phonetics.get(i).text.equals("null")
                            && itemsModelo.phonetics.get(i).text != null){
                                //LO GUARDAMOS PARA SER ENVIADO A FIREBASE AL HISTORIAL
                                h.pronunciacion = itemsModelo.phonetics.get(i).text;
                                textPronunciacion = itemsModelo.phonetics.get(i).text;
                                break;
                            }
                        }

                        //PARA OBTENER DEFINICION
                        //RECORRER LA LISTA DE MEANING
                        for(int i = 0; i< itemsModelo.meanings.size();i++){
                            //RECORRER LA LISTE DE DEFINICIONES DENTRO DE MEANING
                            for(int j = 0; j<itemsModelo.meanings.get(i).definitions.size();j++){
                                //COMPROBAR QUE HALLA UNA DEFINICION
                                if(!(""+itemsModelo.meanings.get(i).definitions.get(j).definition).equals("null")
                                        && !itemsModelo.meanings.get(i).definitions.get(j).definition.isEmpty()
                                && itemsModelo.meanings.get(i).definitions.get(j).definition != null
                                && !itemsModelo.meanings.get(i).definitions.get(j).definition.equals("null")){
                                    h.significado = itemsModelo.meanings.get(i).definitions.get(j).definition;
                                    tv_definicion.setText("Definitions: "+itemsModelo.meanings.get(i).definitions.get(j).definition);
                                    encontro = true;
                                    break;
                                }
                            }
                            if(encontro){
                                break;
                            }
                        }
                    }


                    //PARA GUARDAR LA PALABRA EN EL HISTORIAL
                    referenciaData.child("HISTORIAL").child(correoAntesDeDominio).child(h.palabra).setValue(h);

                }
                //al no obtener datos
                else{
                    //se asigna valor
                    ejemplo.setText("Palabra no encontrada");

                    Toast.makeText(Buscar.this, "PALABRA NO ENCONTRADA", Toast.LENGTH_SHORT).show();

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
            ejemplo.setText("Audio no encontrado");

            Toast.makeText(Buscar.this, "AUDIO NO ENCONTRADO", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, ""+textPronunciacion, Toast.LENGTH_SHORT).show();
        }
    }

    public void btnHistorial(View view) {
        Intent i = new Intent(this, Historial.class);
        i.putExtra("USUARIO",correoAntesDeDominio);
        startActivity(i);
    }

    public void btnCerrarSesion(View view) {
        auth.signOut();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
    public void btnPerfil(View view){
        Intent i = new Intent(this, Perfil.class);
        startActivity(i);
    }

}