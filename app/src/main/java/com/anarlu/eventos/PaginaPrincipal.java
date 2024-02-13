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
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_principal);

        // Configurar la Toolbar
        toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
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
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_pg, new EventosFragment()).commit();
        // Configurar la Toolbar inicialmente con el primer fragmento
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        // Configurar la Toolbar inicialmente con el primer fragmento
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Configurar la Toolbar después de inflar el menú
        updateToolbar("Eventos", true, true);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Manejar los clics en los elementos del menú de la Toolbar aquí si es necesario
        return super.onOptionsItemSelected(item);
    }
    // Método para actualizar dinámicamente la Toolbar
    private void updateToolbar(String title, boolean showSettingsIcon, boolean showProfileIcon) {
        getSupportActionBar().setTitle(title);
        toolbar.getMenu().findItem(R.id.action_settings).setVisible(showSettingsIcon);
        toolbar.getMenu().findItem(R.id.action_profile).setVisible(showProfileIcon);
    }
}