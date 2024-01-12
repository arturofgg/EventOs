package com.example.eventos;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class Menu extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prueba_menu);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Maneja los eventos de clic en las opciones del menú aquí
                switch (item.getItemId()) {
                    case R.id.menu_option1:
                        // Acción para la opción 1
                        // Puedes agregar código para manejar la selección de la opción 1
                        break;
                    case R.id.menu_option2:
                        // Acción para la opción 2
                        // Puedes agregar código para manejar la selección de la opción 2
                        break;
                    // Agrega más casos según sea necesario
                }

                // Cierra el menú después de hacer clic en una opción
                drawerLayout.closeDrawers();

                return true;
            }
        });
    }
}
