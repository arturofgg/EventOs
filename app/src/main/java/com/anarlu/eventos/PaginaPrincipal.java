package com.anarlu.eventos;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

public class PaginaPrincipal extends AppCompatActivity { // Cambio aquí a BaseActivity

    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_principal);

        viewPager = findViewById(R.id.viewPagerPaginaPrincipal);
        // Configurar el adaptador de fragmentos para el ViewPager
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new MisEventosFragment();
                    case 1:
                        return new ChatFragment();
                    case 2:
                        return new EventosFragment();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_option1) {
                    viewPager.setCurrentItem(0);
                    return true;
                } else if (itemId == R.id.nav_option2) {
                    viewPager.setCurrentItem(1);
                    return true;
                } else if (itemId == R.id.nav_option3) {
                    viewPager.setCurrentItem(2);
                    return true;
                }
                return false;
            }
        });

        // Configurar el ViewPager para sincronizarse con la selección del BottomNavigationView
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.nav_option1);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.nav_option2);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.nav_option3);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        // Cargar el primer fragmento por defecto
        viewPager.setCurrentItem(0);
    }
}
