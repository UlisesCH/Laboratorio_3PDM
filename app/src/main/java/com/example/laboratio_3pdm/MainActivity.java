package com.example.laboratio_3pdm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public EditText TxtCorreo, TxtContra;
    public Dialog popRegistrar;
    public FirebaseAuth autenticacion;
    public FirebaseDatabase database;
    public DatabaseReference referenciData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autenticacion = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        referenciData = database.getReference();

        popRegistrar = new Dialog(this);
        popRegistrar.setContentView(R.layout.pop_registrar);

        TxtCorreo = findViewById(R.id.TxtCorreo);
        TxtContra = findViewById(R.id.TxtContra);

    }

    public void ClickIniciar(View v){

        String Correo = TxtCorreo.getText().toString();
        String Contra = TxtContra.getText().toString();

        autenticacion.signInWithEmailAndPassword(Correo, Contra)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    Toast.makeText(MainActivity.this, "SESION INICIADA", Toast.LENGTH_SHORT).show();
                    FirebaseUser usuario = autenticacion.getCurrentUser();

                    Log.d("Usuario ", usuario.getEmail());

                    Intent intent = new Intent(MainActivity.this, Buscar.class);
                    startActivity(intent);

                }
            }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    public void ClickRegistrar(View v){

        Button btnEliminar = popRegistrar.findViewById(R.id.BtnReRegistrar);
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText TxtDue = popRegistrar.findViewById(R.id.TxtDUE),
                         TxtNombre = popRegistrar.findViewById(R.id.TxtNombre),
                         TxtReCorreo = popRegistrar.findViewById(R.id.TxtReCorreo),
                         TxtReContra = popRegistrar.findViewById(R.id.TxtReContra);

                Log.d("Correo ", TxtReCorreo.getText().toString());
                Log.d("Contra ", TxtReContra.getText().toString());
                String Correo = TxtReCorreo.getText().toString();
                String Contra = TxtReContra.getText().toString();

                Usuario usuario = new Usuario();

                autenticacion.createUserWithEmailAndPassword(Correo, Contra)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                usuario.DUE = TxtDue.getText().toString();
                                usuario.Nombre = TxtNombre.getText().toString();
                                usuario.Correo = Correo;

                                referenciData.child("USUARIOS").child(String.valueOf(usuario.DUE)).setValue(usuario);

                                Toast.makeText(MainActivity.this, "USUARIO CREADO CORRECTAMENTE", Toast.LENGTH_SHORT).show();

                                TxtDue.setText("");
                                TxtNombre.setText("");
                                TxtReContra.setText("");
                                TxtReCorreo.setText("");
                                TxtCorreo.setText(Correo);
                                TxtContra.setText(Contra);

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "USUARIO NO FUE CREADO CORRECTAMENTE "+e.toString(), Toast.LENGTH_SHORT).show();
                            Log.d("error", e.toString());
                        }
                    });

                popRegistrar.dismiss();

            }
        });

        popRegistrar.show();

    }

}