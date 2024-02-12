package com.anarlu.eventos;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PaginaPrincipal extends BaseActivity { // Cambio aquí a BaseActivity

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_principal);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();
                if (itemId == R.id.nav_option1) {
                    selectedFragment = new EventosFragment();
                } else if (itemId == R.id.nav_option2) {
                    selectedFragment = new ChatFragment();
                } else if (itemId == R.id.nav_option3) {
                    selectedFragment = new MisEventosFragment();
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });

        // Cargar el primer fragmento por defecto
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EventosFragment()).commit();
    }
}
