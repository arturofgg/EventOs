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
    private Button forgetPass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        login=view.findViewById(R.id.buttonLogin);
        mail=view.findViewById(R.id.emailInput);
        pass=view.findViewById(R.id.passwordInput);
        mAuth=FirebaseAuth.getInstance();
        forgetPass=view.findViewById(R.id.forgetPass);

        //de login a register
        Button registerButton = view.findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener la actividad actual y cambiar al fragmento de registro
                LRFragmentsActivity activity = (LRFragmentsActivity) getActivity();
                activity.getViewPager().setCurrentItem(1);
            }
        });

        //de login a recuperar contraseña
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irRecuperarContrasena();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailUser=mail.getText().toString().trim();
                String passUser=pass.getText().toString().trim();

                if(emailUser.isEmpty() || passUser.isEmpty()){
                    Toast.makeText(getActivity(), R.string.ToastCampos, Toast.LENGTH_SHORT).show();
                }else{
                    loginUser(emailUser,passUser);
                }
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
                    Toast.makeText(getActivity(), R.string.Welcome, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), R.string.ToastUserPass, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //CAMBIAR LA CLASE
    private void irEventos() {
        Intent intent=new Intent(getActivity(),LRFragmentsActivity.class);
        startActivity(intent);
    }

    private void irRecuperarContrasena() {
        Intent intent = new Intent(getActivity(), RecuperarContrasenaActivity.class);
        startActivity(intent);
    }
}