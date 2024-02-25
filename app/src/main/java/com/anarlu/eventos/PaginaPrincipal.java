package com.anarlu.eventos;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PaginaPrincipal extends AppCompatActivity {
    private boolean doubleBackToExitPressedOnce = false;
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private Menu menu;
    private MenuItem menuItem;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_principal);

        mFirestore= FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("606138593322-qmo8r77q8faabttijt0tj9e6aiai0rtm.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        menu=toolbar.getMenu();

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
                    invalidateOptionsMenu();
                    return true;
                } else if (itemId == R.id.nav_option2) {
                    viewPager.setCurrentItem(1);
                    invalidateOptionsMenu();
                    return true;
                } else if (itemId == R.id.nav_option3) {
                    viewPager.setCurrentItem(2);
                    invalidateOptionsMenu();
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
                        toolbar.setTitle(R.string.my_events);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.nav_option2);
                        toolbar.setTitle(R.string.chat);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.nav_option3);
                        toolbar.setTitle(R.string.events);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        // Cargar el primer fragmento por defecto
        viewPager.setCurrentItem(0);

        // Establecer el título de la aplicación en la barra de herramientas
        getSupportActionBar().setTitle(R.string.my_events);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        menuItem = menu.findItem(R.id.action_profile);

        // Obtener el usuario actual
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Verificar si el usuario está autenticado
        if (currentUser != null) {
            // Obtener el nombre de usuario (este sería el nombre de la imagen en Storage)
            String userName = currentUser.getUid(); // o el campo que contenga el nombre único

            // Construir la referencia a la imagen en Firebase Storage
            StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("perfil/").child(userName);

            // Descargar la imagen y establecerla como ícono del perfil en el menú
            profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Utilizar Glide u otra biblioteca para cargar la imagen desde la URL y establecerla en el ícono del menú
                    Glide.with(getApplicationContext())
                            .load(uri)
                            .circleCrop()
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    // Establecer la imagen como ícono del perfil en el menú
                                    menuItem.setIcon(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    // No es necesario hacer nada aquí
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Manejar el caso en el que no se pueda descargar la imagen
                    Log.e(TAG, "Error al descargar la imagen del perfil: " + e.getMessage());
                }
            });
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile) {
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent=new Intent(PaginaPrincipal.this, Ajustes.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            moveTaskToBack(true);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Presiona ATRÁS de nuevo para salir", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private void irLogin(){
        Intent intent=new Intent(PaginaPrincipal.this, LRFragmentsActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentItem", viewPager.getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewPager.setCurrentItem(savedInstanceState.getInt("currentItem"));
    }

}
