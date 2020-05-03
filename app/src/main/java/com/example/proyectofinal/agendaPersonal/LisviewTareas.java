package com.example.proyectofinal.agendaPersonal;


public class LisviewTareas {
   private int imagen,idPersonal ;
   private String nombreTarea, hora;
   private String fechaTareas;
    private String descripcion;


    public LisviewTareas(String nombreTarea, String fechaTareas, int imagen,int idPersonal,String hora,String descripcion) {
        this.imagen = imagen;
        this.nombreTarea = nombreTarea;
        this.fechaTareas = fechaTareas;
        this.hora = hora;
        this.idPersonal = idPersonal;
        this.descripcion=descripcion;

    }
    public int getImagen() {
        return imagen;
    }

    public int getIdPersonal() {
        return idPersonal;
    }

    public String getNombreTarea() {
        return nombreTarea;
    }

    public String getHora() {
        return hora;
    }

    public String getFechaTareas() {
        return fechaTareas;
    }
    public String getDescripcion() {
        return descripcion;
    }

}

