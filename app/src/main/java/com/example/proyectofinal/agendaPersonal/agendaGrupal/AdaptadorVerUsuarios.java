package com.example.proyectofinal.agendaPersonal.agendaGrupal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectofinal.R;

import com.example.proyectofinal.agendaPersonal.agendaCompartida.ClaseBuscarUsuarios;


import java.util.List;

public class AdaptadorVerUsuarios extends RecyclerView.Adapter implements View.OnClickListener {
    private Context context;
    private List<ClaseBuscarUsuarios> listComponer;
    private  View.OnClickListener listener;

    public AdaptadorVerUsuarios(Context context, List<ClaseBuscarUsuarios> listUsuarios) {
        this.context = context;
        this.listComponer = listUsuarios;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View contenView = LayoutInflater.from(context).inflate(R.layout.items_ver_usuarios,null);

        contenView.setOnClickListener(this);

        return new MyHolder(contenView);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ClaseBuscarUsuarios item = listComponer.get(position);
        AdaptadorVerUsuarios.MyHolder holder1 =(AdaptadorVerUsuarios.MyHolder)holder;
        holder1.ivFoto.setImageResource(item.getImagenUsuarios());
        holder1.tvTitulo.setText(item.getNombreUsuarios());

    }

    @Override
    public int getItemCount() {
        return listComponer.size();
    }
    public static class MyHolder extends RecyclerView.ViewHolder {

        ImageView ivFoto;
        TextView tvTitulo;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            ivFoto =(ImageView) itemView.findViewById(R.id.imageVerUsuarios);
            tvTitulo = (TextView) itemView.findViewById(R.id.idUsuarios);


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
