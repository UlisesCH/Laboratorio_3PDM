package com.example.laboratio_3pdm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Perfil extends AppCompatActivity {

    private ImageView img;
    private EditText et_nombre, et_carrera, et_due;
    private FirebaseDatabase database;
    private DatabaseReference referenciaData;
    //EN ESTA VARIABLE ALMACENAREMOS SOLO LA PARTE DEL CORREO ANTES DEL @
    private String correoAntesDeDominio = "";
    private String correoUsuario;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        img = (ImageView) findViewById(R.id.imageView);
        et_nombre = (EditText) findViewById(R.id.etPerfilNombre);
        et_carrera = (EditText) findViewById(R.id.etPerfilCarrera);
        et_due = (EditText) findViewById(R.id.etPerfilDUE);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        usuario();
        referenciaData = database.getReference("USUARIOS").child(correoAntesDeDominio);
        cargarDatos();

    }

    public void usuario(){
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
    }

    public void cargarDatos(){
        referenciaData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario u = snapshot.getValue(Usuario.class);
                Glide.with(getApplicationContext()).load(u.img).into(img);
                et_nombre.setText(u.Nombre);
                et_carrera.setText(u.Carrera);
                et_due.setText(u.DUE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ONCANCELLED", "Failed to read value: "+error.toException());
            }
        });
    }

}