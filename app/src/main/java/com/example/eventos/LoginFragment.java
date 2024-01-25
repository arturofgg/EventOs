package com.example.eventos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        //de login a register
        Button registerButton = view.findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //indico dentro del array de LRFragments que al darle click a donde ira es a register
                ((LRFragmentsActivity) getActivity()).viewPager.setCurrentItem(1);
            }
        });

        //de login a recuperar contraseña
        Button forgetPassButton = view.findViewById(R.id.forgetPass);
        forgetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //indico dentro del array de LRFragments que al darle click a donde ira es a recuperar contraseña

                ((LRFragmentsActivity) getActivity()).viewPager.setCurrentItem(2);
            }
        });

        return view;
    }
}
