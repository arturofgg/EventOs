package com.example.eventos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

//NO UTIL ACTUALMENTE
public class Login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void openSignup(View v) {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    }
}