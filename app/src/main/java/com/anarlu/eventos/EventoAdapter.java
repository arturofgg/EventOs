package com.anarlu.eventos;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {
    private List<Evento> eventos;
    private FirebaseFirestore mFirestore;

    public EventoAdapter(List<Evento> eventos){
        this.eventos=eventos;
    }

    @Override
    public EventoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_evento, parent, false);
        return new EventoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EventoViewHolder holder, int position) {
        ImageView event_image=holder.itemView.findViewById(R.id.event_image);
        mFirestore=FirebaseFirestore.getInstance();
        Evento evento = eventos.get(position);
        holder.nombreEvento.setText(evento.getNombre());
        String tipo=evento.getTipo();
        // Configura los listeners para tus botones aquí
        holder.botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Eliminar Evento")
                        .setMessage("¿Estás seguro que quiere eliminar este evento?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Obtén la posición actual del ViewHolder
                                int currentPosition = holder.getAdapterPosition();

                                // Comprueba si la posición es válida
                                if (currentPosition == RecyclerView.NO_POSITION) {
                                    // La posición no es válida, no hagas nada
                                    return;
                                }
                                // Maneja el clic en el botón eliminar
                                // Obtén el ID del evento que se va a eliminar
                                String idEvento = eventos.get(currentPosition).getID_evento();

                                // Elimina el evento de Firestore
                                mFirestore.collection("Eventos").document(idEvento)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // El evento se eliminó correctamente, actualiza la lista y notifica al adaptador
                                                eventos.remove(currentPosition);
                                                notifyDataSetChanged();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Hubo un error al eliminar el evento, maneja este caso
                                                Toast.makeText(v.getContext(), "Error al eliminar el evento: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("No", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        switch (tipo){
            case "Deporte/Sport":
                event_image.setImageResource(R.drawable.deportes);
                break;
            case "Senderismo/Hiking":
                event_image.setImageResource(R.drawable.senderismo);
                break;
            case "Fiesta/Party":
                event_image.setImageResource(R.drawable.fiesta);
                break;
            case "Paseo/Walk":
                event_image.setImageResource(R.drawable.paseo);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    public static class EventoViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreEvento;
        public Button botonEliminar;

        public EventoViewHolder(View itemView) {
            super(itemView);
            nombreEvento = itemView.findViewById(R.id.event_name);
            botonEliminar = itemView.findViewById(R.id.delete_event);
        }
    }
}
