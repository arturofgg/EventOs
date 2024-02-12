package com.anarlu.eventos;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class LoginFragment extends Fragment {
    private Button login;
    private EditText pass,mail;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Habilitar el deslizamiento a la izquierda cuando se carga la vista
        ((LRFragmentsActivity) getActivity()).viewPager.setEnableSwipeLeft(false);
        // Habilitar el deslizamiento a la derecha cuando se carga la vista
        ((LRFragmentsActivity) getActivity()).viewPager.setEnableSwipeRight(true);

        Button registerButton = view.findViewById(R.id.register);
        login=view.findViewById(R.id.buttonLogin);
        mail=view.findViewById(R.id.emailInput);
        pass=view.findViewById(R.id.passwordInput);
        mAuth=FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailUser=mail.getText().toString().trim();
                String passUser=pass.getText().toString().trim();

                if(emailUser.isEmpty() || passUser.isEmpty()){
                    Toast.makeText(getActivity(), "Rellene todos los campos por favor", Toast.LENGTH_SHORT).show();
                }else{
                    loginUser(emailUser,passUser);
                }
            }
        });

        //de login a register
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Habilitar el deslizamiento a la derecha
                ((LRFragmentsActivity) getActivity()).viewPager.setEnableSwipeRight(true);
                // Cambiar a la página de registro
                ((LRFragmentsActivity) getActivity()).viewPager.setCurrentItem(1);
            }
        });

        //de login a recuperar contraseña
        Button forgetPassButton = view.findViewById(R.id.forgetPass);
        forgetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Deshabilitar el deslizamiento a la derecha
                ((LRFragmentsActivity) getActivity()).viewPager.setEnableSwipeRight(false);
                // Cambiar a la página de recuperación de contraseña
                ((LRFragmentsActivity) getActivity()).viewPager.setCurrentItem(2);
            }
        });
        return view;
    }

    private void loginUser(String emailUser,String passUser){
        mAuth.signInWithEmailAndPassword(emailUser,passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    irEventos();
                    Toast.makeText(getActivity(), "Bienvenido", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    pass.requestFocus();
                    pass.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                    mail.requestFocus();
                    mail.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                    Toast.makeText(getActivity(), "USUARIO O CONTRASEÑA INCORRECTOS", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void irEventos() {
        Intent intent=new Intent(getActivity(),TusEventos.class);
        startActivity(intent);
    }
}