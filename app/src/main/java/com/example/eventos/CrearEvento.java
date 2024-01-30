package com.example.eventos;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrearEvento extends AppCompatActivity {

    private TextView hora_ini,hora_fin;
    private EditText desc,ubic,name;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private Button crear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_evento);

        hora_ini=findViewById(R.id.hora_ini);
        hora_fin=findViewById(R.id.hora_fin);
        mAuth=FirebaseAuth.getInstance();
        mFirestore=FirebaseFirestore.getInstance();
        crear=findViewById(R.id.btnGuardar);
        desc=findViewById(R.id.etDescripcion);
        ubic=findViewById(R.id.etUbicacion);
        name=findViewById(R.id.etNombre);

        hora_ini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        CrearEvento.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Aquí puedes manejar la hora seleccionada
                                String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                                hora_ini.setText(selectedTime);
                            }
                        },
                        // Hora inicial
                        12, 0,
                        // Si el formato de 24 horas debe ser utilizado
                        true);
                timePickerDialog.updateTime(12,0);
                timePickerDialog.show();
            }
        });

        hora_fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        CrearEvento.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Aquí puedes manejar la hora seleccionada
                                String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                                hora_fin.setText(selectedTime);
                            }
                        },
                        // Hora inicial
                        12, 0,
                        // Si el formato de 24 horas debe ser utilizado
                        true);
                timePickerDialog.updateTime(12,0);
                timePickerDialog.show();
            }
        });

        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre=name.getText().toString().trim();
                String ubicacion=ubic.getText().toString().trim();
                String descr=desc.getText().toString().trim();
                String hora_inicial=hora_ini.getText().toString().trim();
                String hora_final=hora_fin.getText().toString().trim();
                if(nombre.isEmpty() || ubicacion.isEmpty() || descr.isEmpty() || hora_inicial.isEmpty() || hora_final.isEmpty()){
                    Toast.makeText(CrearEvento.this, "Rellene todos los campos", Toast.LENGTH_SHORT).show();
                }else {
                    crearEvento(nombre,ubicacion,descr,hora_inicial,hora_final);
                }
            }
        });
    }

    public void crearEvento(String nombre,String ubicacion,String descr,String hora_inicial,String hora_final){
        DocumentReference EventoRef=mFirestore.collection("Eventos").document();
        FirebaseUser user=mAuth.getCurrentUser();
        Map<String, Object> map = new HashMap<>();
        map.put("ID_evento",EventoRef.getId());
        map.put("Nombre", nombre);
        map.put("Ubicacion", ubicacion);
        map.put("Descripicion", descr);
        map.put("Hora_inicio", hora_inicial);
        map.put("Hora_final", hora_final);
        map.put("ID_usuario",user.getUid());

        // Añade el evento a Firestore
        EventoRef.set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Documento añadido con ID: " + EventoRef.getId());
                        finish();
                        irEventos();
                        Toast.makeText(CrearEvento.this, "Evento creado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error al añadir el documento", e);
                    }
                });
    }

    private void irEventos() {
        Intent intent=new Intent(CrearEvento.this,TusEventos.class);
        startActivity(intent);
    }
}