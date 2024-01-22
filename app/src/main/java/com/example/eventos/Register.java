package com.example.eventos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class Register extends AppCompatActivity {

    private Button botonregistro;
    private SignInButton googleR;
    private EditText usuario,correo,password,rpassword;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    private static final int RC_SIGN_IN = 123;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirebaseApp.initializeApp(this);
        mFirestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        usuario=findViewById(R.id.usernameInputR);
        correo=findViewById(R.id.emailInputR);
        password=findViewById(R.id.passwordInputR);
        rpassword=findViewById(R.id.repeatPasswordInputR);
        botonregistro=findViewById(R.id.buttonRegister);
        googleR=findViewById(R.id.googleR);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("90656351526-1hp02rmkk4ip4fnfbboj3b441ml7e1f1.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        googleR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        botonregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameUser=usuario.getText().toString().trim();
                String emailUser=correo.getText().toString().trim();
                String passUser=password.getText().toString().trim();
                String rpassUser=rpassword.getText().toString().trim();

                if(nameUser.isEmpty() || emailUser.isEmpty() || passUser.isEmpty()){
                    Toast.makeText(Register.this,"Por favor rellene todos los campos",Toast.LENGTH_SHORT).show();
                }else{
                    registerUser(nameUser,emailUser,passUser);
                }
            }
        });
    }

    private void registerUser(String nameUser, String emailUser, String passUser) {
        mAuth.createUserWithEmailAndPassword(emailUser, passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                    String id=mAuth.getCurrentUser().getUid();
                    Map<String,Object> map=new HashMap<>();
                    map.put("id",id);
                    map.put("usuario",nameUser);
                    map.put("correo",emailUser);
                    map.put("foto",null);
                Toast.makeText(Register.this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();

                    mFirestore.collection("usuarios").document(id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();
                            openEventos(task);//AQUI IRA LA PANTALLA DE EVENTOS CUANDO ESTE CREADA
                            Toast.makeText(Register.this, "Bienvenido "+nameUser, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "Error al guardar usuario", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Register.this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Error de inicio de sesion", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user=mAuth.getCurrentUser();
                            Uri photoUrl=user.getPhotoUrl();
                            uploadProfileImage(user.getUid(),photoUrl,new OnSuccessListener<Uri>(){
                                public void onSuccess(Uri imageUrl){
                                    Map<String,Object> datosUsuario=new HashMap<>();
                                    datosUsuario.put("id",user.getUid());
                                    datosUsuario.put("usuario",user.getDisplayName());
                                    datosUsuario.put("correo",user.getEmail());
                                    datosUsuario.put("foto",imageUrl.toString());

                                    mFirestore.collection("usuarios").document(user.getUid()).set(datosUsuario).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            irEventos();
                                            // Sign in success, update UI with the signed-in user's information
                                            updateUI(user);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Register.this, "Registro fallido", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });



                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Register.this, "Sign-in failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    private void uploadProfileImage(String userId, Uri profileImageUri, OnSuccessListener<Uri> onSuccessListener) {
                        // Descargar la imagen desde la URL antes de cargarla en Firebase Storage
                        Glide.with(Register.this)
                                .asBitmap()
                                .load(profileImageUri)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                                        // Convertir el Bitmap a un ByteArrayOutputStream
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        resource.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                                        // Crear un InputStream desde el ByteArrayOutputStream
                                        InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());

                                        // Subir la imagen al Storage
                                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                        StorageReference imageRef = storageRef.child("perfil/" + userId);

                                        imageRef.putStream(inputStream)
                                                .addOnSuccessListener(taskSnapshot -> {
                                                    // Obtener la URL de la imagen después de subirla
                                                    imageRef.getDownloadUrl().addOnSuccessListener(onSuccessListener);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("UploadError", "Error al subir archivo", e);
                                                    Toast.makeText(Register.this, "Error al subir archivo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    }

                                });
                    }

                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void openLogin(View v) {
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
    }

    public void openEventos(Task<Void> v){
        Intent intent=new Intent(Register.this,TusEventos.class);
        startActivity(intent);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(FirebaseUser user) {
        user=mAuth.getCurrentUser();
        if(user!=null){
            irEventos();
        }
    }

    private void irEventos() {
        Intent intent=new Intent(Register.this,TusEventos.class);
        startActivity(intent);
    }

}