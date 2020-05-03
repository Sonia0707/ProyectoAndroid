package com.example.proyectofinal.agendaPersonal.agendaCompartida;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectofinal.R;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorPeticiones extends RecyclerView.Adapter implements  View.OnClickListener {

    private Context context;
    private List<ClaseBuscarUsuarios> listUsuarios;
    private  View.OnClickListener listener;

    public AdaptadorPeticiones(Context context, List<ClaseBuscarUsuarios> listUsuarios) {
        this.context = context;
        this.listUsuarios = listUsuarios;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View contenView = LayoutInflater.from(context).inflate(R.layout.items_buscar_amigos,null);

        contenView.setOnClickListener(this);

        return new MyHolder(contenView);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ClaseBuscarUsuarios item = listUsuarios.get(position);
        AdaptadorPeticiones.MyHolder holder1 =(AdaptadorPeticiones.MyHolder)holder;
        holder1.ivFoto.setImageResource(item.getImagenUsuarios());
        holder1.tvTitulo.setText(item.getNombreUsuarios());

    }

    @Override
    public int getItemCount() {
        return listUsuarios.size();
    }
    public static class MyHolder extends RecyclerView.ViewHolder {

        ImageView ivFoto;
        TextView tvTitulo;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            ivFoto =(ImageView) itemView.findViewById(R.id.imageUsuario);
            tvTitulo = (TextView) itemView.findViewById(R.id.tvUsuario);


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
    public void filterList(ArrayList<ClaseBuscarUsuarios> filteredList){
        listUsuarios = filteredList;
        notifyDataSetChanged();
    }


}
