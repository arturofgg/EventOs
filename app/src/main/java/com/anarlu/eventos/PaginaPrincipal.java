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
    private UCrop.Options options;
    private static final int REQUEST_GALLERY_PERMISSION=2020;
    private GoogleSignInClient mGoogleSignInClient;
    private Drawable persona;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_principal);

        mFirestore= FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("606138593322-qmo8r77q8faabttijt0tj9e6aiai0rtm.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        int idImagenPredeterminada = getResources().getIdentifier("person", "drawable", getPackageName());

        if (idImagenPredeterminada != 0) {
            persona = getResources().getDrawable(idImagenPredeterminada);
        } else {
            Toast.makeText(this, "Imagen no dispobible", Toast.LENGTH_SHORT).show();
        }

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
                        toolbar.setTitle("Mis Eventos");
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.nav_option2);
                        toolbar.setTitle("Chat");
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.nav_option3);
                        toolbar.setTitle("Eventos");
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        // Cargar el primer fragmento por defecto
        viewPager.setCurrentItem(0);
    }

    protected void onStart() {
        super.onStart();
        FirebaseUser Fuser = mAuth.getCurrentUser();
        if (Fuser == null) {
            irLogin();
        } else {
            if(Fuser.getPhotoUrl()!=null){
                loadFirebaseImage(Fuser.getPhotoUrl());
            }else {
                Uri imagenUri=obtenerNuevaImagenSeleccionada();
                if(imagenUri!=null){
                    cargarNuevaImagen(imagenUri);
                }else {
                    if(Fuser.getPhotoUrl()!=null){
                        loadFirebaseImage(Fuser.getPhotoUrl());
                    }
                }
            }

            if(menuItem!=null){
                cargarImagenPerfil();
            }

            // Aquí es donde añades el código para descargar la imagen de Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference pathReference = storageRef.child("perfil/"+Fuser.getUid());

            File localFile = null;
            try {
                localFile = File.createTempFile("images", "jpg");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            File finalLocalFile = localFile;
            pathReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // El archivo se ha descargado con éxito
                            Uri downloadedImageUri = Uri.fromFile(finalLocalFile);
                            cargarNuevaImagen(downloadedImageUri);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // El archivo no se ha descargado correctamente
                            Toast.makeText(PaginaPrincipal.this, "El archivo no se ha podido descargar correctamente", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void cargarNuevaImagen(Uri imagenUri) {
        Glide.with(this)
                .load(imagenUri)
                .circleCrop()
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        if (menuItem != null) {
                            menuItem.setIcon(resource);
                        } else {
                            Toast.makeText(PaginaPrincipal.this, "menuItem nulo", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "menuItem es nulo cargar nueva imagen.");
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
        Glide.get(getApplicationContext()).clearMemory();
    }

    private Uri obtenerNuevaImagenSeleccionada() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String imagenUriStr = sharedPref.getString("imagenUri", null);
        if (imagenUriStr != null) {
            return Uri.parse(imagenUriStr);
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        options.setCircleDimmedLayer(true);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY_PERMISSION) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    Glide.with(this)
                            .load(imageUri)
                            .circleCrop()
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    menuItem.setIcon(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });

                    Uri destinoUri = Uri.fromFile(new File(getCacheDir(), "imagenRecortada"));
                    UCrop.of(imageUri, destinoUri).withOptions(options).start(this);
                } else {
                    // Puede que haya casos donde la imagenUri sea nula, debes manejarlo adecuadamente
                    Toast.makeText(this, "Error al obtener la imagen de la cámara", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            // Manejar el error del recorte
            final Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Log.e("UCrop", "Error al recortar la imagen", cropError);
                Toast.makeText(this, "Error al recortar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleCropResult(Intent data) {
        final Uri resultUri = UCrop.getOutput(data);

        if (resultUri != null) {
            Glide.with(this)
                    .load(resultUri)
                    .circleCrop()
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            if (menuItem != null) {
                                menuItem.setIcon(resource);
                            } else {
                                Toast.makeText(PaginaPrincipal.this, "menuItem nulo", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "menuItem es nulo. handle crop.");
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
            Glide.get(getApplicationContext()).clearMemory();
            cambiarFotoPerfil(resultUri);
        } else {
            Toast.makeText(this, "Error al obtener la imagen recortada", Toast.LENGTH_SHORT).show();
        }
    }

    private void subirFoto(Uri resultUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        StorageReference storageRef = storage.getReference();
        StorageReference fotoRef = storageRef.child("perfil/" + userId);

        // Eliminar la foto anterior
        fotoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Eliminar foto", "Eliminacion exitosa");

                // Subir la nueva foto después de eliminar la anterior
                UploadTask uploadTask = fotoRef.putFile(resultUri);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String fotoUrl = uri.toString();
                                Map<String, Object> datosUsuario = new HashMap<>();
                                datosUsuario.put("foto", fotoUrl);

                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                if (currentUser != null && mFirestore!=null) {
                                    mFirestore.collection("usuarios").document(currentUser.getUid()).update(datosUsuario).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(PaginaPrincipal.this, "Foto guardada correctamente", Toast.LENGTH_SHORT).show();
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setPhotoUri(Uri.parse(fotoUrl)) // Aquí debes poner la URL de la nueva foto
                                                    .build();

                                            currentUser.updateProfile(profileUpdates)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d(TAG, "User profile updated.");
                                                            }
                                                        }
                                                    });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(PaginaPrincipal.this, "Error al actualizar la foto", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PaginaPrincipal.this, "Error al subir la nueva foto", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Eliminar foto", "Error al eliminar foto:" + e.getMessage());
                Toast.makeText(PaginaPrincipal.this, "Error al eliminar foto anterior: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cambiarFotoPerfil(Uri nuevaImagenUri) {
        subirFoto(nuevaImagenUri);
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            guardarImagen(nuevaImagenUri,currentUser.getUid());
        }
    }

    private void guardarImagen(Uri imagenUri,String userId) {
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null && currentUser.getUid().equals(userId)) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("imagenUri", imagenUri.toString());
            editor.apply();
        }
    }

    private void loadFirebaseImage(Uri photoUrl){
        Glide.with(this)
                .load(photoUrl)
                .circleCrop()
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        if (menuItem != null) {
                            menuItem.setIcon(resource);
                        } else {
                            // Manejar el caso en que menuItem sea nulo
                            // Por ejemplo, mostrar un mensaje de error o registrar el problema
                            Toast.makeText(PaginaPrincipal.this, "menuItem es nulo", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "menuItem es nulo. loadfirebaseimage.");
                        }

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
        Glide.get(getApplicationContext()).clearMemory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        menuItem = menu.findItem(R.id.action_profile);
        if (menuItem != null) {
            cargarImagenPerfil();
        } else {
            Log.e(TAG, "menuItem es nulo en onCreateOptionsMenu.");
        }
        return true;
    }


    public void cargarImagenPerfil() {
        FirebaseUser Fuser = mAuth.getCurrentUser();
        if (Fuser != null) {
            if(Fuser.getPhotoUrl()!=null){
                loadFirebaseImage(Fuser.getPhotoUrl());
            }else {
                Uri imagenUri=obtenerNuevaImagenSeleccionada();
                if(imagenUri!=null){
                    cargarNuevaImagen(imagenUri);
                }else {
                    // Si no hay una imagen guardada localmente, cargamos una imagen predeterminada
                    cargarImagenPredeterminada();
                }
            }
        }
    }

    public void cargarImagenPredeterminada() {
        // Aquí es donde cargas tu imagen predeterminada
        int idImagenPredeterminada = getResources().getIdentifier("person", "drawable", getPackageName());
        if (idImagenPredeterminada != 0) {
            Drawable imagenPredeterminada = getResources().getDrawable(idImagenPredeterminada);
            menuItem.setIcon(imagenPredeterminada);
        } else {
            Toast.makeText(this, "Imagen predeterminada no disponible", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile) {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, REQUEST_GALLERY_PERMISSION);
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
