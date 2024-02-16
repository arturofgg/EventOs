package com.anarlu.eventos;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yalantis.ucrop.UCrop;

public class MisEventosFragment extends Fragment {
    private RecyclerView recyclerView;
    private EventoAdapter adapter;
    private List<Evento> eventos;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private ImageView user2, creacion;
    private Toolbar toolbar;

    private UCrop.Options options;

    private GoogleSignInClient mGoogleSignInClient;
    private Drawable persona;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_eventos, container, false);

        // Configuración de la Toolbar
        toolbar = view.findViewById(R.id.toolbarMisEventos);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        // Configurar el título personalizado
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Mis Eventos");

        // Indicar que este fragmento tiene su propio menú de opciones
        setHasOptionsMenu(true);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        eventos = new ArrayList<>();
        adapter = new EventoAdapter(eventos);
        recyclerView.setAdapter(adapter);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user2 = view.findViewById(R.id.imageView2);
        options = new UCrop.Options();
        creacion = view.findViewById(R.id.imageView5);

        FirebaseApp.initializeApp(/*context=*/ getActivity());
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());

        user2.setScaleType(ImageView.ScaleType.FIT_XY);

        options.setCircleDimmedLayer(true);
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("90656351526-1hp02rmkk4ip4fnfbboj3b441ml7e1f1.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        int idImagenPredeterminada = getResources().getIdentifier("person", "drawable", getActivity().getPackageName());

        if (idImagenPredeterminada != 0) {
            persona = getResources().getDrawable(idImagenPredeterminada);
        } else {
            Toast.makeText(getActivity(), "Imagen no disponible", Toast.LENGTH_SHORT).show();
        }

        creacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrearNuevoEvento();
            }
        });

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            mFirestore.collection("Eventos").whereEqualTo("ID_usuario", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        eventos.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Evento evento = document.toObject(Evento.class);
                            eventos.add(evento);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void CrearNuevoEvento() {
        Intent intent = new Intent(getActivity(), CrearEvento.class);
        startActivity(intent);
    }
}