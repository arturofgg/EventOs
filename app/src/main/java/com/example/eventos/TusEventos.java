package com.example.eventos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;


public class TusEventos extends AppCompatActivity {

    private Button logout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private ImageView user2;
    private static final int PICK_IMAGE_REQUEST=1513;
    private static final int REQUEST_CAMERA_PERMISSION=2020;

    private UCrop.Options options;

    private GoogleSignInClient mGoogleSignInClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tus_eventos);


        mAuth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.logout);
        user2 = findViewById(R.id.imageView2);
        options = new UCrop.Options();

        user2.setScaleType(ImageView.ScaleType.FIT_XY);

        options.setCircleDimmedLayer(true);
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("90656351526-1hp02rmkk4ip4fnfbboj3b441ml7e1f1.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        user2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, REQUEST_CAMERA_PERMISSION);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logout();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser Fuser = mAuth.getCurrentUser();
        if (Fuser == null) {
            irLogin();
        } else {
            Uri photoUrl=Fuser.getPhotoUrl();
            if(photoUrl!=null){
                Glide.with(this).load(photoUrl).placeholder(R.drawable.gradientesplash).circleCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(user2);
            }
            Uri imagenUri = cargarImagen();
            if (imagenUri != null) {
                Glide.with(this).load(imagenUri).placeholder(R.drawable.gradientesplash).circleCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(user2);
            }
        }
    }

// Cargar imagen de perfil al iniciar sesión
    private void cargarImagenDePerfil(Uri nuevaImagen) {
        subirFoto(nuevaImagen);
        guardarImagen(nuevaImagen);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Obtener la URL de la imagen de Firestore
            mFirestore.collection("usuarios")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String fotoUrl = documentSnapshot.getString("foto");
                        if (fotoUrl != null) {
                            // Cargar la imagen en el ImageView utilizando Glide
                            Glide.with(this)
                                    .load(fotoUrl)
                                    .placeholder(R.drawable.gradientesplash)
                                    .circleCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(user2);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Manejar errores al recuperar la URL de la imagen
                        Toast.makeText(this, "Error al cargar la imagen de perfil", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void logout() {
        mAuth.signOut();
        mGoogleSignInClient.signOut();
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        irLogin();
    }

    private void irLogin(){
        Intent intent=new Intent(TusEventos.this,Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        options.setCircleDimmedLayer(true);
        if(resultCode==RESULT_OK) {
            if (requestCode==REQUEST_CAMERA_PERMISSION) {
                Uri imageUri=data.getData();
                user2.setImageURI(imageUri);
                Uri destinoUri = Uri.fromFile(new File(getCacheDir(), "imagenRecortada"));
                UCrop.of(imageUri, destinoUri).withOptions(options).start(this);
            }
        }else if (requestCode==UCrop.REQUEST_CROP && resultCode==RESULT_OK) {
            Uri resultUri=UCrop.getOutput(data);
            Glide.with(this).load(resultUri).placeholder(R.drawable.gradientesplash).circleCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(user2);
            cambiarFotoPerfil(resultUri);
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
                                if (currentUser != null) {
                                    mFirestore.collection("usuarios").document(currentUser.getUid()).update(datosUsuario).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(TusEventos.this, "Foto guardada correctamente", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(TusEventos.this, "Error al actualizar la foto", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TusEventos.this, "Error al subir la nueva foto", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Eliminar foto", "Error al eliminar foto:" + e.getMessage());
                Toast.makeText(TusEventos.this, "Error al eliminar foto anterior: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void cambiarFotoPerfil(Uri nuevaImagenUri) {
        subirFoto(nuevaImagenUri);
        guardarImagen(nuevaImagenUri);
    }

    private void guardarImagen(Uri imagenUri) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("imagenUri", imagenUri.toString());
        editor.apply();
    }

    private Uri cargarImagen() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String imagenUriStr = sharedPref.getString("imagenUri", null);
        if (imagenUriStr != null) {
            return Uri.parse(imagenUriStr);
        } else {
            return null;
        }
    }



}