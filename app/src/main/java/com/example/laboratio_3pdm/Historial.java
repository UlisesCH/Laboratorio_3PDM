package com.example.laboratio_3pdm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.laboratio_3pdm.modelo.modeloHistorial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Historial extends AppCompatActivity {

    private ListView lv_historial;
    private ArrayList<modeloHistorial> listData;
    public FirebaseDatabase database;
    public DatabaseReference referenciaData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        lv_historial = (ListView) findViewById(R.id.lvHistorial);
        listData = new ArrayList<modeloHistorial>();

        database = FirebaseDatabase.getInstance();
        referenciaData = database.getReference();
        referenciaData.child("HISTORIAL").addValueEventListener(listHistorial);

        //EVENTO AL TOCAR ITEM
        lv_historial.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), Buscar.class);
                intent.putExtra("PALABRA",listData.get(i).palabra);
                startActivity(intent);
                finish();
            }
        });
    }

    public ValueEventListener listHistorial = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot items:snapshot.getChildren()) {
                modeloHistorial h = items.getValue(modeloHistorial.class);
                listData.add(h);
            }
            adaptadorHistorial ap = new adaptadorHistorial(listData,getApplicationContext());
            lv_historial.setAdapter(ap);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
}