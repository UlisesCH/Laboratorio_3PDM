package com.example.laboratio_3pdm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Perfil extends AppCompatActivity {

    public FirebaseStorage storage;
    public StorageReference reference;
    public Uri urlImage;
    public ImageView img;

    public TextView DUE, NombreUsuario, Correo, Carrera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();
        img = findViewById(R.id.ImgPerfil);

        DUE = findViewById(R.id.TxtPerDUE);
        NombreUsuario = findViewById(R.id.TxtPerNombre);
        Correo = findViewById(R.id.TxtPerCorreo);
        Carrera = findViewById(R.id.TxtPerCarrera);

        // Se asigna los valores enviados del Main
        DUE.setText(getIntent().getStringExtra("DUE"));
        NombreUsuario.setText(getIntent().getStringExtra("Nombre"));
        Correo.setText(getIntent().getStringExtra("Correo"));
        Carrera.setText(getIntent().getStringExtra("Carrera"));

        CargarPerfil();

    }

    public void CargarPerfil(){

        StorageReference file = reference.child("ImagenesPrueba/").child(getIntent().getStringExtra("DUE"));

        Log.d("Nombre ", "Hola Car");

        file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(getApplicationContext()).load(uri).into(img);

            }
        });

    }

    public void clickAbrirGaleria(View v){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/");
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){

            urlImage = data.getData();

            StorageReference file = reference.child("ImagenesPrueba").child(getIntent().getStringExtra("DUE"));

            UploadTask subir = file.putFile(urlImage);

            Task<Uri> uriTask = subir.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return file.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri urlDescarga = task.getResult();
                    Log.i("URLIMAGEN", urlDescarga.toString());
                    Glide.with(getApplicationContext()).load(urlDescarga).into(img);
                }
            });
        }
    }


}