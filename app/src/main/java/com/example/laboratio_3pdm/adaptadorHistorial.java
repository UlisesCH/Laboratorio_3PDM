package com.example.laboratio_3pdm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.laboratio_3pdm.modelo.modeloHistorial;

import java.util.ArrayList;

public class adaptadorHistorial extends BaseAdapter {
    public ArrayList<modeloHistorial> data;
    public Context context;

    public adaptadorHistorial(ArrayList<modeloHistorial> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        modeloHistorial m = (modeloHistorial)getItem(i);
        view = LayoutInflater.from(context).inflate(R.layout.items_historial,null);
        TextView palabra = (TextView) view.findViewById(R.id.textView);
        TextView ejemplo = (TextView) view.findViewById(R.id.textView4);
        TextView pronunciacion = (TextView) view.findViewById(R.id.textView2);
        TextView significado = (TextView) view.findViewById(R.id.textView3);

        palabra.setText("word: "+m.palabra);
        ejemplo.setText("example: "+m.ejemplo);
        pronunciacion.setText("phonetics: "+m.pronunciacion);
        significado.setText("definition: "+m.significado);
        return view;
    }
}
