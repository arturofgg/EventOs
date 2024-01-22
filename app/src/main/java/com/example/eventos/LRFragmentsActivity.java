package com.example.eventos;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class LRFragmentsActivity extends AppCompatActivity {

    public ViewPager viewPager;  // Ahora es público
    private Fragment[] fragments = {new LoginFragment(), new RegisterFragment()};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lrfragments);

        viewPager = (ViewPager) findViewById(R.id.fragment_container);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });

        // Establecer la transformación de página personalizada:
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
