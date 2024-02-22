package com.anarlu.eventos;

import android.content.DialogInterface;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
        mFirestore=FirebaseFirestore.getInstance();
        Evento evento = eventos.get(position);
        holder.nombreEvento.setText(evento.getNombre());
        holder.nombreUsuario.setText("Creado por:"+evento.getNombre_usuario());
        holder.ubicacion.setText("Ubicacion:"+evento.getUbicacion());
        holder.descripcion.setText(evento.getDescripicion());
        holder.fecha_inicio.setText("Fecha de inicio:"+evento.getFecha_inicial());
        holder.fecha_fin.setText("Fecha de finalizacion"+evento.getFecha_final());
        holder.hora_inicio.setText("Hora de inicio:"+evento.getHora_inicio());
        holder.hora_fin.setText("Hora de finalizacion:"+evento.getHora_final());
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
        public Button desplegar;

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

            cardContent.setVisibility(View.GONE);

            desplegar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cardContent.toggle();
                }
            });
        }
    }
    public void updateData(List<Evento> newEventos) {
        this.eventos = newEventos;
        notifyDataSetChanged();
    }
}
