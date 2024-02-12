package com.anarlu.eventos;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class LRFragmentsActivity extends AppCompatActivity {

    //El viewpager sirve para moverse entre los fragemnts que peretenecen a esta seccion.
    public CustomViewPager viewPager;
    //Array de fragments para moverse por estas paginas
    private Fragment[] fragments = {new LoginFragment(), new RegisterFragment(), new RecuperarContrasenaFragment()};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lrfragments);

        viewPager = findViewById(R.id.fragment_container);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new LoginFragment();
                    case 1:
                        return new RegisterFragment();
                    case 2:
                        return new RecuperarContrasenaFragment();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        });

        // Establecer el fragmento actual como el de inicio de sesión (índice 0)
        viewPager.setCurrentItem(0);

        // Añadir un OnPageChangeListener
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) { // Si el fragmento actual es el de registro
                    viewPager.setEnableSwipeRight(false); // Deshabilitar deslizamiento a la derecha
                } else {
                    viewPager.setEnableSwipeRight(true); // Habilitar deslizamiento a la derecha para otros fragmentos
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Establecer la transformación de página personalizada (se puede cambiar):
        viewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
            private static final float MIN_SCALE = 0.85f;
            private static final float MIN_ALPHA = 0.5f;

            @Override
            public void transformPage(View view, float position) {
                if (position < -1 || position > 1) {
                    view.setAlpha(MIN_ALPHA);
                    view.setScaleX(MIN_SCALE);
                    view.setScaleY(MIN_SCALE);
                } else {
                    float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);
                    view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
                }
            }
        });
    }
}
