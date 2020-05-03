package com.example.proyectofinal.agendaPersonal.agendaGrupal;

public class ClaseListaGrupos {

    private int idAdmin;
    private int idGrupalG;
    private int imagenG;
    private String nombreG;
    private String nombreP;
    private String fecha;

    public ClaseListaGrupos(int imagenG,int idAdmin, int idGrupalG, String nombreG, String nombreP, String fecha) {
        this.idAdmin = idAdmin;
        this.idGrupalG = idGrupalG;
        this.nombreG = nombreG;
        this.nombreP = nombreP;
        this.fecha = fecha;
        this.imagenG=imagenG;
    }
    public int getImagenG() {
        return imagenG;
    }

    public int getIdAdmin() {
        return idAdmin;
    }

    public int getIdGrupalG() {
        return idGrupalG;
    }

    public String getNombreG() {
        return nombreG;
    }

    public String getNombreP() {
        return nombreP;
    }

    public String getFecha() {
        return fecha;
    }


}
