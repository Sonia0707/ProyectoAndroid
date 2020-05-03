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

import java.util.List;

public class AdaptadorGrupos extends RecyclerView.Adapter implements View.OnClickListener {

    private Context context;
    private List<ClaseListaGrupos> listaG;
    private  View.OnClickListener listener;

    public AdaptadorGrupos(Context context, List<ClaseListaGrupos> listaG) {
        this.context = context;
        this.listaG = listaG;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contenView = LayoutInflater.from(context).inflate(R.layout.itmes_lista_grupos,null);

        contenView.setOnClickListener(this);

        return new HolderG(contenView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ClaseListaGrupos item = listaG.get(position);
        AdaptadorGrupos.HolderG holder1 =(AdaptadorGrupos.HolderG)holder;
        holder1.ivFoto.setImageResource(item.getImagenG());
        holder1.nombGrupo.setText(item.getNombreG());
        holder1.fecha.setText(item.getFecha());
        holder1.nombPersona.setText(item.getNombreP());
    }
    @Override
    public int getItemCount() {
        return listaG.size();
    }

    public static class HolderG extends RecyclerView.ViewHolder {

        ImageView ivFoto;
        TextView nombGrupo;
        TextView fecha;
        TextView nombPersona;

        public HolderG(@NonNull View itemView) {
            super(itemView);
            ivFoto =(ImageView) itemView.findViewById(R.id.imageGrupo);
            nombGrupo = (TextView) itemView.findViewById(R.id.nombGrupo);
            fecha = (TextView) itemView.findViewById(R.id.FechaTareasG);
            nombPersona = (TextView) itemView.findViewById(R.id.nombPersona);

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
