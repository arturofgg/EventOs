package com.anarlu.eventos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarContrasenaFragment extends Fragment {

    private EditText emailInput;
    private Button resetPasswordButton;
    private FirebaseAuth mAuth;
    private String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recuperar_contrasena, container, false);

        mAuth=FirebaseAuth.getInstance();
        emailInput = view.findViewById(R.id.emailInput);
        resetPasswordButton = view.findViewById(R.id.resetPasswordButton);
        //reset de la password
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailInput.getText().toString();
                if (!email.isEmpty()) {
                    // Implemetar logica de la contraseña
                    ResetPassword();
                } else {
                    Toast.makeText(getActivity(), "Por favor, introduce tu email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // De recuperar contraseña a inicio de sesión
        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LRFragmentsActivity) getActivity()).viewPager.setCurrentItem(0);
            }
        });

        return view;
    }

    private void ResetPassword(){
        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getActivity(), "Enlace de nueva contraseña enviado", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}

