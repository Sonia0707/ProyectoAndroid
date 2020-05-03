package com.example.proyectofinal.agendaPersonal.agendaCompartida;

public class ClaseAmigos {

    private int imagenAmigos, idUsuario2;
    private String nombreAmigos;

    public ClaseAmigos(int imagenAmigos, String nombreAmigos,int idUsuario2) {
        this.imagenAmigos = imagenAmigos;
        this.nombreAmigos = nombreAmigos;
        this.idUsuario2 = idUsuario2;
    }

    public int getImagenAmigos() {
        return imagenAmigos;
    }

    public String getNombreAmigos() {
        return nombreAmigos;
    }

    public int getIdUsuario2() {
        return idUsuario2;
    }
}
