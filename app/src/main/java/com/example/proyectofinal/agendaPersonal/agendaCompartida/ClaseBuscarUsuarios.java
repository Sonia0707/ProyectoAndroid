package com.example.proyectofinal.agendaPersonal.agendaCompartida;

public class ClaseBuscarUsuarios {
    private int imagenUsuarios, idUsuario2;
    private String nombreUsuarios;

    public ClaseBuscarUsuarios(int imagenUsuarios, String nombreUsuarios, int idUsuario2) {
        this.imagenUsuarios = imagenUsuarios;
        this.nombreUsuarios = nombreUsuarios;
        this.idUsuario2 = idUsuario2;

    }

    public int getImagenUsuarios() {
        return imagenUsuarios;
    }

    public String getNombreUsuarios() {
        return nombreUsuarios;
    }

    public int getIdUsuario2() {
        return idUsuario2;
    }


}
