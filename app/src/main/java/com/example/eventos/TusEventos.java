package com.example.eventos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;


import com.yalantis.ucrop.UCrop;

import de.hdodenhof.circleimageview.CircleImageView;

public class TusEventos extends AppCompatActivity {

    private Button logout;
    private FirebaseAuth mAuth;
    private ImageView user;
    private static final int REQUEST_CODE_GALLERY=1513;
    private static final int REQUEST_CAMERA_PERMISSION=2020;

    private UCrop.Options options;

    private Uri imagenUriCamara;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tus_eventos);

        mAuth=FirebaseAuth.getInstance();
        logout=findViewById(R.id.logout);
        user=findViewById(R.id.imageView2);
        options=new UCrop.Options();

        user.setScaleType(ImageView.ScaleType.FIT_XY);

        options.setCircleDimmedLayer(true);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCamara();
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
        irLogin();
    }

    private void irLogin(){
        Intent intent=new Intent(TusEventos.this,Login.class);
        startActivity(intent);
        finish();
    }

    private void cambiarImagen() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            abrirCamara();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void abrirCamara(){
        Intent galleryintent=new Intent(Intent.ACTION_PICK);
        galleryintent.setType("image/*");
        Intent cameraintent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent chooser=Intent.createChooser(galleryintent,"Eliga opcion");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS,new Intent[]{cameraintent});
        startActivityForResult(chooser,REQUEST_CODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        options.setCircleDimmedLayer(true);
        if(requestCode==REQUEST_CODE_GALLERY && resultCode==RESULT_OK) {
            Uri imagenSeleccionada;
            if(data!=null && data.getData()!=null){
                imagenSeleccionada=data.getData();
            }
            else{
                imagenSeleccionada=imagenUriCamara;
            }
            Uri destinoUri = Uri.fromFile(new File(getCacheDir(), "imagenRecortada"));
            UCrop.of(imagenSeleccionada, destinoUri).withOptions(options).withMaxResultSize(137, 139).start(this);
        }
        else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            Uri resultUri=UCrop.getOutput(data);
            Glide.with(this).load(resultUri).circleCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(user);
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