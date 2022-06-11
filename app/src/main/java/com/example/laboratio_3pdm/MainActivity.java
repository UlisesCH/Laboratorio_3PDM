package com.example.laboratio_3pdm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public EditText TxtDue, TxtNombre, TxtCorreo, TxtContra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TxtCorreo = findViewById(R.id.TxtCorreo);
        TxtContra = findViewById(R.id.TxtContra);

    }

    public void ClickIniciar(View v){

        Intent intent = new Intent(this, Buscar.class);
        startActivity(intent);

    }

    public void ClickRegistrar(View v){

        String Correo = TxtCorreo.getText().toString();
        String Contra = TxtContra.getText().toString();

    }

}