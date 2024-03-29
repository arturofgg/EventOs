package com.anarlu.eventos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        openApp();

        ImageView etos = findViewById(R.id.etossplash);
        Animation etoss = AnimationUtils.loadAnimation(this, R.anim.vanish);
        etos.startAnimation(etoss);

        ImageView ven = findViewById(R.id.vensplash);

        ven.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Animación de rotación durante 4 segundos con pausa de 0.5 segundos entre cada vuelta
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.ven2); // Coloca aquí tu imagen de placeholder
                Glide.with(splash.this)
                        .load(R.drawable.ven2) // Coloca aquí tu imagen
                        .apply(requestOptions)
                        .into(ven);

                Animation rotateAnimation = AnimationUtils.loadAnimation(splash.this, R.anim.move);
                ven.startAnimation(rotateAnimation);
            }
        }, 2000);


        ImageView brujula = findViewById(R.id.brujulasplash);
        Animation compass = AnimationUtils.loadAnimation(this, R.anim.fadeanimation);
        brujula.startAnimation(compass);

        brujula.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Animación de rotación durante 4 segundos con pausa de 0.5 segundos entre cada vuelta
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.brujulabien); // Coloca aquí tu imagen de placeholder
                Glide.with(splash.this)
                        .load(R.drawable.brujulabien) // Coloca aquí tu imagen
                        .apply(requestOptions)
                        .into(brujula);

                Animation rotateAnimation = AnimationUtils.loadAnimation(splash.this, R.anim.rotacion);
                brujula.startAnimation(rotateAnimation);
            }
        }, 1000);
    }

        private void openApp() {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run() {
                    Intent intent = new Intent(splash
                            .this, LRFragmentsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }, 5000);
        }
}