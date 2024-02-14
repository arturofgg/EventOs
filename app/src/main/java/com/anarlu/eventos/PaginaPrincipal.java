package com.anarlu.eventos;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PaginaPrincipal extends AppCompatActivity { // Cambio aquí a BaseActivity

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
                String title = "";
                int itemId = item.getItemId();
                if (itemId == R.id.nav_option1) {
                    selectedFragment = new MisEventosFragment();
                } else if (itemId == R.id.nav_option2) {
                    selectedFragment = new ChatFragment();
                } else if (itemId == R.id.nav_option3) {
                    selectedFragment = new EventosFragment();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_pg, selectedFragment).commit();
                return true;
            }
        });

        // Cargar el primer fragmento por defecto
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_pg, new MisEventosFragment()).commit();
    }
}