package com.example.laboratio_3pdm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public EditText TxtCorreo, TxtContra;
    public Dialog popRegistrar;
    public FirebaseAuth autenticacion;
    public FirebaseDatabase database;
    public DatabaseReference referenciData;
    public String [] datos;
    private String correoAntesDeDominio = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autenticacion = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        referenciData = database.getReference();

        datos = new String[3];

        popRegistrar = new Dialog(this);
        popRegistrar.setContentView(R.layout.pop_registrar);

        TxtCorreo = findViewById(R.id.TxtCorreo);
        TxtContra = findViewById(R.id.TxtContra);

    }

    public void ClickIniciar(View v){
        //VALIDAR QUE CAMPOS NO ESTEN VACIOS
        if(!TxtCorreo.getText().toString().isEmpty() && !TxtContra.getText().toString().isEmpty()){
            String Correo = TxtCorreo.getText().toString();
            String Contra = TxtContra.getText().toString();

            autenticacion.signInWithEmailAndPassword(Correo, Contra)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                FirebaseUser usuario = autenticacion.getCurrentUser();
                                Log.d("Usuario ", usuario.getEmail());
                                Intent intent = new Intent(getApplicationContext(), Buscar.class);
                                intent.putExtra("PALABRA", "");
                                startActivity(intent);
                                finish();

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("E",e.toString());
                    if(e.toString().equals("com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted.")){
                        Toast.makeText(MainActivity.this, "CORREO NO REGISTRADO", Toast.LENGTH_SHORT).show();
                    }else if(e.toString().equals("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The password is invalid or the user does not have a password.")){
                        Toast.makeText(MainActivity.this, "CONTRASEÑA INCORRECTA", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else{
            Toast.makeText(this, "DEBE DE LLENAR TODOS LOS CAMPOS", Toast.LENGTH_SHORT).show();
        }

    }




    public void ClickRegistrar(View v){

        Button BtnReRegistrar = popRegistrar.findViewById(R.id.BtnReRegistrar);
        BtnReRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText TxtDue = popRegistrar.findViewById(R.id.TxtDUE),
                         TxtNombre = popRegistrar.findViewById(R.id.TxtNombre),
                         TxtCarrera = popRegistrar.findViewById(R.id.TxtCarrera),
                         TxtReCorreo = popRegistrar.findViewById(R.id.TxtReCorreo),
                         TxtReContra = popRegistrar.findViewById(R.id.TxtReContra), 
                et_segundaContrasenia = popRegistrar.findViewById(R.id.etRegistrarSegundaContrasenia);
                
                //VALIDAR QUE NINGUN CAMPO ESTE VACIO
                if(!TxtDue.getText().toString().isEmpty() && !TxtNombre.getText().toString().isEmpty()
                && !TxtCarrera.getText().toString().isEmpty() && !TxtReCorreo.getText().toString().isEmpty()
                && !TxtReContra.getText().toString().isEmpty() && !et_segundaContrasenia.getText().toString().isEmpty()){
                    
                    //VALIDAR QUE LAS CONTRASEÑAS COINCIDAD
                    if(TxtReContra.getText().toString().equals(et_segundaContrasenia.getText().toString())){
                        String Correo = TxtReCorreo.getText().toString();
                        String Contra = TxtReContra.getText().toString();
                        
                        //VALIDAR QUE LA CONTRASEÑA TENGA UNA LONGITUD MAYOR O IGUAL A 6 CARACTERES
                        if(TxtReContra.getText().toString().length() >= 6){

                            Usuario usuario = new Usuario();

                            autenticacion.createUserWithEmailAndPassword(Correo, Contra)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            if (task.isSuccessful()){

                                                usuario.DUE = TxtDue.getText().toString();
                                                usuario.Nombre = TxtNombre.getText().toString();
                                                usuario.Carrera = TxtCarrera.getText().toString();
                                                usuario.Correo = Correo;
                                                usuario.img = "https://firebasestorage.googleapis.com/v0/b/laboratorio3-fee75.appspot.com/o/perfil.png?alt=media&token=fe95c870-7e30-4e89-bb36-4332a5b131ed";

                                                //OBTENER EL CORREO DEL USUARIO CON SESION INICIADA TODO LO QUE ESTA ANTES DEL @
                                                for(int i = 0; i < Correo.length();i++){
                                                    if(Correo.charAt(i) == '@'){
                                                        break;
                                                    }else{
                                                        correoAntesDeDominio += Correo.charAt(i);
                                                    }

                                                }
                                                referenciData.child("USUARIOS").child(String.valueOf(correoAntesDeDominio)).setValue(usuario);

                                                Toast.makeText(MainActivity.this, "USUARIO CREADO CORRECTAMENTE", Toast.LENGTH_SHORT).show();

                                                TxtDue.setText("");
                                                TxtNombre.setText("");
                                                TxtCarrera.setText("");
                                                TxtReContra.setText("");
                                                TxtReCorreo.setText("");
                                                et_segundaContrasenia.setText("");
                                                TxtCorreo.setText(Correo);
                                                TxtContra.setText(Contra);

                                            }

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if(e.toString().equals("com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.")){
                                        Toast.makeText(MainActivity.this, "EL CORREO YA SE ENCUENTRA REGISTRADO CON UNA CUENTA", Toast.LENGTH_LONG).show();
                                    }
                                    Log.d("error", e.toString());
                                }
                            });

                            popRegistrar.dismiss();                            
                        }else{
                            Toast.makeText(MainActivity.this, "Contraseña muy pequeña", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(MainActivity.this, "Las contraseñas no son iguales", Toast.LENGTH_SHORT).show();
                    }
                    
                }else{
                    Toast.makeText(MainActivity.this, "Debe de llenar todos los campos", Toast.LENGTH_SHORT).show();
                }
                

            }
        });

        popRegistrar.show();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = autenticacion.getCurrentUser();
        if(currentUser != null){
            Toast.makeText(this, "Sesion esta iniciada", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, Buscar.class);
            i.putExtra("PALABRA", "");
            startActivity(i);
        }
    }

}