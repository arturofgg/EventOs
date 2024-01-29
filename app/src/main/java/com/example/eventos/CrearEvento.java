package com.example.eventos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class CrearEvento extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_evento);

        Spinner spinnerHora = findViewById(R.id.spinnerHora);

        List<String> horasDelDia = new ArrayList<>();
        horasDelDia.add("8:00 ");
        horasDelDia.add("9:00");
        horasDelDia.add("10:00");
        horasDelDia.add("11:00");
        horasDelDia.add("12:00");
        horasDelDia.add("13:00");
        horasDelDia.add("14:00");
        horasDelDia.add("15:00");
        horasDelDia.add("16:00");
        horasDelDia.add("17:00");
        horasDelDia.add("18:00");
        horasDelDia.add("19:00");
        horasDelDia.add("20:00");
        horasDelDia.add("21:00");
        horasDelDia.add("22:00");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, horasDelDia);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerHora.setAdapter(adapter);
    }
}