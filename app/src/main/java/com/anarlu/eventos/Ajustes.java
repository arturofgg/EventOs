package com.anarlu.eventos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

public class Ajustes extends AppCompatActivity {

    private Button logout,borrar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        logout = findViewById(R.id.logout);
        borrar=findViewById(R.id.delete);

        Toolbar toolbar = findViewById(R.id.AppBar);
        setSupportActionBar(toolbar);
        // Habilitar la flecha de retroceso
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("606138593322-qmo8r77q8faabttijt0tj9e6aiai0rtm.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Ajustes.this)
                        .setTitle("Cerrar Sesión")
                        .setMessage("¿Estás seguro que quiere cerrar sesión?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                            }
                        })
                        .setNegativeButton("No", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Ajustes.this)
                        .setTitle("Borrar cuenta")
                        .setMessage("¿Estás seguro que quiere borrar su cuenta? Esta accion no tendrá marcha atrás.")
                        .setPositiveButton("Sí, estoy seguro", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                borrarUsuario();
                            }
                        })
                        .setNegativeButton("No", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    private void logout() {
        mAuth.signOut();
        mGoogleSignInClient.signOut();
        Intent i = this.getPackageManager().getLaunchIntentForPackage(this.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        irLogin();
    }

    private void borrarUsuario(){
        FirebaseUser user=mAuth.getCurrentUser();
        String idUser=user.getUid();

        mFirestore.collection("usuarios").document(idUser)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Ajustes.this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
                        irLogin();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Ajustes.this, "Error al eliminar usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void irLogin(){
        Intent intent=new Intent(Ajustes.this, LRFragmentsActivity.class);
        startActivity(intent);
    }
}