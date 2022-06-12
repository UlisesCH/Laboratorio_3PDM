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
import android.widget.Toast;

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
    public FirebaseStorage storage;
    private StorageReference reference;
    //EN ESTA VARIABLE ALMACENAREMOS SOLO LA PARTE DEL CORREO ANTES DEL @
    private String correoAntesDeDominio = "";
    private String correoUsuario;
    private FirebaseAuth auth;
    public Uri urlImage;
    private Usuario u;

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
        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();
        usuario();
        referenciaData = database.getReference("USUARIOS").child(correoAntesDeDominio);
        cargarDatos();



        //EVENTO PARA CAMBIAR FOTO AL TOCARLA
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent,1);
            }
        });

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
                u = snapshot.getValue(Usuario.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            urlImage = data.getData();
            StorageReference file = reference.child("PERFILES").child(correoAntesDeDominio).child(urlImage.getLastPathSegment());
            UploadTask subir = file.putFile(urlImage);
            Task<Uri> uriTask = subir.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return file.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri urlDescarga = task.getResult();

                    //ACTUALIZAR LA URL DE IMAGEN DEL PERFIL
                    Usuario user = new Usuario();
                    user.Nombre = u.Nombre;
                    user.Carrera = u.Carrera;
                    user.Correo = u.Correo;
                    user.DUE = u.DUE;
                    user.img = urlDescarga.toString();
                    referenciaData.setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Perfil.this, "Imagen actualizada", Toast.LENGTH_SHORT).show();
                                    cargarDatos();
                                }
                            });
                }
            });
        }
    }

    public void btnModificar(View view){
        //VALIDAR QUE LOS CAMPOS NO ESTEN VACIOS
        if(!et_nombre.getText().toString().isEmpty() && !et_carrera.getText().toString().isEmpty()
        && !et_due.getText().toString().isEmpty()){
            Usuario user = new Usuario();
            user.Nombre = et_nombre.getText().toString();
            user.Carrera = et_carrera.getText().toString();
            user.Correo = u.Correo;
            user.DUE = et_due.getText().toString();
            user.img = u.img;
            referenciaData.setValue(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            cargarDatos();
                            Toast.makeText(Perfil.this, "DATOS MODIFICADOS CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(this, "DEBE LLENAR TODOS LOS CAMPOS", Toast.LENGTH_SHORT).show();
        }
        
    }

}