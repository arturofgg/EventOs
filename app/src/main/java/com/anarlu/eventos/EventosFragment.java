package com.anarlu.eventos;

import static android.content.ContentValues.TAG;

import android.app.Instrumentation;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventosFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventoAdapter adapter;
    private List<Evento> eventos;
    private FirebaseFirestore mFirestore;
    private Toolbar toolbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_eventos,container,false);

        // Configuración de la Toolbar
        toolbar = view.findViewById(R.id.toolbarEventos);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        // Configurar el título personalizado
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Eventos");

        // Indicar que este fragmento tiene su propio menú de opciones
        setHasOptionsMenu(true);

        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFirestore=FirebaseFirestore.getInstance();

        eventos=new ArrayList<>();
        adapter=new EventoAdapter(eventos);
        recyclerView.setAdapter(adapter);

            mFirestore.collection("Eventos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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



        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}