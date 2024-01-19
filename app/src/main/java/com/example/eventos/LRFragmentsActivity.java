package com.example.eventos;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class LRFragmentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lrfragments);

        // Carga el fragmento de inicio de sesión al iniciar la actividad
        loadFragment(new LoginFragment());
    }

    public void loadFragment(Fragment fragment) {
        // Crea una transacción de fragmento
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Reemplaza el contenedor actual con el nuevo fragmento
        transaction.replace(R.id.fragment_container, fragment);

        // Añade la transacción a la pila de retroceso
        transaction.addToBackStack(null);

        // Realiza la transacción
        transaction.commit();
    }
}