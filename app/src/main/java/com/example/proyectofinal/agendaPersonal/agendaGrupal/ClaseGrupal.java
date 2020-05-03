package com.example.proyectofinal.agendaPersonal.agendaGrupal;

public class ClaseGrupal {

    private int imagen,idGrupal ;
    private String titulo, hora,nombrePersona,fechaTareas,descripcion;

    public ClaseGrupal(int imagen, int idGrupal, String titulo, String hora, String nombrePersona, String fechaTareas, String descripcion) {
        this.imagen = imagen;
        this.idGrupal = idGrupal;
        this.titulo = titulo;
        this.hora = hora;
        this.nombrePersona = nombrePersona;
        this.fechaTareas = fechaTareas;
        this.descripcion = descripcion;
    }
    public int getImagen() {
        return imagen;
    }

    public int getIdGrupal() {
        return idGrupal;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getHora() {
        return hora;
    }

    public String getNombrePersona() {
        return nombrePersona;
    }

    public String getFechaTareas() {
        return fechaTareas;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
