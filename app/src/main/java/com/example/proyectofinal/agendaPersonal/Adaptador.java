package com.example.proyectofinal.agendaPersonal;


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

public class Adaptador extends RecyclerView.Adapter
        implements View.OnClickListener{
    private List<LisviewTareas> listatareas;
    Context context;

    public Adaptador(List<LisviewTareas> listatareas, Context context) {
        this.listatareas = listatareas;
        this.context = context;
    }

    private  View.OnClickListener listener;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contenView = LayoutInflater.from(context).inflate(R.layout.item_tareas,null);


        contenView.setOnClickListener(this);

        return new MyHolder(contenView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LisviewTareas item = listatareas.get(position);
        MyHolder holder1 =(MyHolder)holder;
        holder1.ivFoto.setImageResource(listatareas.get(position).getImagen());
        holder1.tvTitulo.setText(listatareas.get(position).getNombreTarea());
        holder1.fecha.setText(listatareas.get(position).getFechaTareas());

    }

    @Override
    public int getItemCount() {
        return listatareas.size();
    }


    public static class MyHolder extends RecyclerView.ViewHolder {

        ImageView ivFoto;
        TextView tvTitulo;
        TextView fecha;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            ivFoto =(ImageView) itemView.findViewById(R.id.imageTarea);
            tvTitulo = (TextView) itemView.findViewById(R.id.Titulo);
            fecha = (TextView) itemView.findViewById(R.id.FechaTareas);

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
