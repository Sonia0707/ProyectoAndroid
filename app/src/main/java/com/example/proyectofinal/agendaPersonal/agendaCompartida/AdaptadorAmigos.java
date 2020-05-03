package com.example.proyectofinal.agendaPersonal.agendaCompartida;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectofinal.R;

import java.util.List;

public class AdaptadorAmigos extends RecyclerView.Adapter implements View.OnClickListener {

    private Context context;
    private List<ClaseAmigos>Amigos;
    private  View.OnClickListener listener;

    public AdaptadorAmigos(Context context, List<ClaseAmigos> amigos) {
        this.context = context;
        Amigos = amigos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contenView = LayoutInflater.from(context).inflate(R.layout.items_usuarios,null);

        contenView.setOnClickListener(this);

        return new MyHolder(contenView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ClaseAmigos item = Amigos.get(position);
        AdaptadorAmigos.MyHolder holder1 =(AdaptadorAmigos.MyHolder)holder;
        holder1.ivFoto.setImageResource(item.getImagenAmigos());
        holder1.tvTitulo.setText(item.getNombreAmigos());
    }

    @Override
    public int getItemCount() {
        return Amigos.size();
    }
    public static class MyHolder extends RecyclerView.ViewHolder {

        ImageView ivFoto;
        TextView tvTitulo;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            ivFoto =(ImageView) itemView.findViewById(R.id.imageAmigo);
            tvTitulo = (TextView) itemView.findViewById(R.id.tvAmigo);

        }
    }

    public void setOnItemClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null){
            listener.onClick(v);
        }

    }
}
