package com.anarlu.eventos;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

public class EventosAdapterExpandible  extends RecyclerView.Adapter<EventosAdapterExpandible.EventoViewHolder> {

    private List<Evento> eventos;
    private FirebaseFirestore mFirestore;

    public EventosAdapterExpandible(List<Evento> eventos){
        this.eventos=eventos;
    }

    @Override
    public EventosAdapterExpandible.EventoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_eventos_general, parent, false);
        return new EventoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EventosAdapterExpandible.EventoViewHolder holder, int position) {
        ImageView event_image=holder.itemView.findViewById(R.id.event_image);
        mFirestore=FirebaseFirestore.getInstance();
        Evento evento = eventos.get(position);
        holder.nombreEvento.setText(evento.getNombre());
        holder.nombreUsuario.setText("Creado por:"+evento.getNombre_usuario());
        holder.ubicacion.setText(evento.getUbicacion());
        holder.descripcion.setText(evento.getDescripicion());
        holder.fecha_inicio.setText("Fecha de inicio:"+evento.getFecha_inicial());
        holder.fecha_fin.setText("Fecha de finalizacion"+evento.getFecha_final());
        holder.hora_inicio.setText("Hora de inicio:"+evento.getHora_inicio());
        holder.hora_fin.setText("Hora de finalizacion:"+evento.getHora_final());
        String tipo=evento.getTipo();

        switch (tipo){
            case "Deporte":
                event_image.setImageResource(R.drawable.deportes);
                break;
            case "Senderismo":
                event_image.setImageResource(R.drawable.senderismo);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    public static class EventoViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreEvento;
        public TextView nombreUsuario;
        public TextView descripcion;
        public TextView fecha_inicio;
        public TextView fecha_fin;
        public TextView hora_inicio;
        public TextView hora_fin;
        public TextView ubicacion;
        public ExpandableLayout cardContent;
        public Button desplegar,contraer;
        public Button maps;
        public String ubi;
        private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

        public EventoViewHolder(View itemView) {
            super(itemView);
            nombreEvento = itemView.findViewById(R.id.event_name);
            nombreUsuario=itemView.findViewById(R.id.user_name);
            descripcion=itemView.findViewById(R.id.descripcion);
            fecha_inicio=itemView.findViewById(R.id.fecha_inicio);
            fecha_fin=itemView.findViewById(R.id.fecha_finalizacion);
            hora_inicio=itemView.findViewById(R.id.hora_inicio);
            hora_fin=itemView.findViewById(R.id.hora_finalizacion);
            ubicacion=itemView.findViewById(R.id.ubicacion);
            cardContent=itemView.findViewById(R.id.card_content);
            desplegar=itemView.findViewById(R.id.desplegar);
            contraer=itemView.findViewById(R.id.contraer);
            maps=itemView.findViewById(R.id.geo);

            cardContent.setVisibility(View.GONE);

            ubi=ubicacion.getText().toString();

            desplegar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    desplegar.setVisibility(View.GONE);
                    contraer.setVisibility(View.VISIBLE);
                    cardContent.toggle();
                }
            });

            contraer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contraer.setVisibility(View.GONE);
                    desplegar.setVisibility(View.VISIBLE);
                    cardContent.toggle();
                }
            });

            maps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Obtener la ubicación del evento
                    String location = ubicacion.getText().toString();

                    // Crear una Uri para la ubicación
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));

                    // Crear un Intent para ver la ubicación en Google Maps
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps"); // Esto asegura que se abra en Google Maps

                    // Verificar si la aplicación de Google Maps está instalada
                    PackageManager packageManager = v.getContext().getPackageManager();
                    if (mapIntent.resolveActivity(packageManager) != null) {
                        // Si está instalada, abrir Google Maps
                        Log.d("EventoViewHolder", "Google Maps está instalado en el dispositivo");
                        v.getContext().startActivity(mapIntent);
                    } else {
                        // Si no está instalada, mostrar un mensaje al usuario
                        Log.d("EventoViewHolder", "Google Maps no está instalado en este dispositivo");
                        Toast.makeText(v.getContext(), "Google Maps no está instalado en este dispositivo", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    }
    public void updateData(List<Evento> newEventos) {
        this.eventos = newEventos;
        notifyDataSetChanged();
    }
}
