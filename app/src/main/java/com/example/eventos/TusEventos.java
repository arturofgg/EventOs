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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.util.Date;


import com.yalantis.ucrop.UCrop;


public class TusEventos extends AppCompatActivity {

    private Button logout;
    private FirebaseAuth mAuth;
    private ImageView user;
    private static final int REQUEST_CODE_GALLERY=1513;
    private static final int REQUEST_CAMERA_PERMISSION=2020;

    private UCrop.Options options;

    private GoogleSignInClient mGoogleSignInClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tus_eventos);

        mAuth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.logout);
        user = findViewById(R.id.imageView2);
        options = new UCrop.Options();

        user.setScaleType(ImageView.ScaleType.FIT_XY);

        options.setCircleDimmedLayer(true);
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("90656351526-1hp02rmkk4ip4fnfbboj3b441ml7e1f1.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        user.setOnClickListener(new View.OnClickListener() {
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
                Glide.with(this).load(photoUrl).circleCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(user);
            }
            Uri imagenUri = cargarImagen();
            if (imagenUri != null) {
                Glide.with(this).load(imagenUri).circleCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(user);
            }
        }
    }


    private void logout() {
        mAuth.signOut();
        mGoogleSignInClient.signOut();
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
        ImageView user2=findViewById(R.id.imageView2);
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
            Glide.with(this).load(resultUri).circleCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(user2);
            guardarImagen(resultUri);
        }
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