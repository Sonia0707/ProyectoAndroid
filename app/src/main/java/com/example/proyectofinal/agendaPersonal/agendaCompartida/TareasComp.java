package com.example.proyectofinal.agendaPersonal.agendaCompartida;

public class TareasComp {

    private int imagen2;
    private int idComp;
    private int idUsuario2 ;
    private String titulo, horaComp;
    private String fechTareas;
    private String descrip;
    private String nombPersona;

    public TareasComp(int imagen2, int idComp, String titulo, String horaComp, String fechTareas, String descripcion, String nombPersona,int idUsuario2) {
        this.imagen2 = imagen2;
        this.idComp = idComp;
        this.idUsuario2=idUsuario2;
        this.titulo = titulo;
        this.horaComp = horaComp;
        this.fechTareas = fechTareas;
        this.descrip= descripcion;
        this.nombPersona = nombPersona;
    }

    public int getIdUsuario2() {
        return idUsuario2;
    }

    public int getImagen2() {
        return imagen2;
    }

    public int getIdComp() {
        return idComp;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getHoraComp() {
        return horaComp;
    }

    public String getFechTareas() {
        return fechTareas;
    }

    public String getDescrip() {
        return descrip;
    }

    public String getNombPersona() {
        return nombPersona;
    }



}
