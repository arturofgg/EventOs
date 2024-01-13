package com.example.eventos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {

    private Button botonregistro;
    private EditText usuario,correo,password,rpassword;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;


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
                    map.put("password",passUser);

                Toast.makeText(Register.this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();

                    mFirestore.collection("usuarios").document(id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();
                            openLoginR(task);//AQUI IRA LA PANTALLA DE EVENTOS CUANDO ESTE CREADA
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

    public void openLogin(View v) {
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
    }

    public void openLoginR(Task<Void> v){
        Intent intent=new Intent(Register.this,Login.class);
        startActivity(intent);
    }
}