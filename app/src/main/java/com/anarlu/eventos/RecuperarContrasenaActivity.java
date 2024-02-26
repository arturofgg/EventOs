package com.anarlu.eventos;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarContrasenaActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button resetPasswordButton;
    private FirebaseAuth mAuth;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);

        mAuth = FirebaseAuth.getInstance();
        emailInput = findViewById(R.id.emailInputRC);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        //reset de la password
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailInput.getText().toString();
                if (!email.isEmpty()) {
                    // Implemetar logica de la contraseña
                    resetPassword();
                } else {
                    Toast.makeText(RecuperarContrasenaActivity.this, R.string.PleaseEmail, Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }
    private void resetPassword(){
        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(RecuperarContrasenaActivity.this, R.string.LinkPassword, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Manejar el fallo de enviar el correo de restablecimiento de contraseña
                Toast.makeText(RecuperarContrasenaActivity.this, R.string.ErrorLinkPassword, Toast.LENGTH_SHORT).show();
            }
        });
    }
}


