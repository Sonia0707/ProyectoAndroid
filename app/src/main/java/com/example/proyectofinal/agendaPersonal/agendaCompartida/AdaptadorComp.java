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

public class AdaptadorComp extends RecyclerView.Adapter implements View.OnClickListener {

    private Context context;
    private List<TareasComp> tareasComp;
    private  View.OnClickListener listener;

    public AdaptadorComp(Context context, List<TareasComp> tareasComp) {
        this.context = context;
        this.tareasComp = tareasComp;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contenView = LayoutInflater.from(context).inflate(R.layout.item_tareas,null);


        contenView.setOnClickListener(this);

        return new MyHolder(contenView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        TareasComp item = tareasComp.get(position);
        AdaptadorComp.MyHolder holder1 =(AdaptadorComp.MyHolder)holder;
        holder1.ivFoto.setImageResource(item.getImagen2());
        holder1.tvTitulo.setText(item.getTitulo());
        holder1.fecha.setText(item.getFechTareas());

    }

    @Override
    public int getItemCount() {
        return tareasComp.size();
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
