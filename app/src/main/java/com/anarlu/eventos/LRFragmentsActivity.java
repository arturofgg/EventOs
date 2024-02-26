package com.anarlu.eventos;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class LRFragmentsActivity extends AppCompatActivity {
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lrfragments);

        ViewPager viewPager = findViewById(R.id.fragment_container_LR);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new LoginFragment();
                    case 1:
                        return new RegisterFragment();
                    default:
                        return null;
                }
            }
            @Override
            public int getCount() {
                return 2;
            }


        });


        // Establecer el fragmento actual como el de inicio de sesión (índice 0)
        viewPager.setCurrentItem(0);

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

    public ViewPager getViewPager() {
        return findViewById(R.id.fragment_container_LR);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.BackExit, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}