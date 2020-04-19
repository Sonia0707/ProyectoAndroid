package com.example.proyectofinal.agendaPersonal;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.proyectofinal.R;
import com.example.proyectofinal.agendaPersonal.agendaCompartida.ListaAmigosTareas;
import com.example.proyectofinal.agendaPersonal.agendaCompartida.PeticionAmigos;
import com.example.proyectofinal.agendaPersonal.agendaCompartida.VerPetiones;
import com.example.proyectofinal.agendaPersonal.agendaGrupal.Lista_Grupos;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Usuario extends AppCompatActivity implements View.OnClickListener {
    //Crear varibles para visualizar y las que necesitemos:
    ImageView btnPersonal;
    ImageView btnCompartida;
    ImageView btnGrupal;
    ImageView principal;
    Button btnCerrar;
    FloatingActionButton btnVerPeticionUsuario,btnBuscarAmigosUsuario;
    int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);
        btnPersonal= (ImageView) findViewById(R.id.btnPersonal);
        btnCompartida= (ImageView) findViewById(R.id.btnCompartida);
        btnGrupal= (ImageView) findViewById(R.id.btnGrupal);
        btnVerPeticionUsuario= (FloatingActionButton) findViewById(R.id.buscarPeticiones);
        btnBuscarAmigosUsuario= (FloatingActionButton) findViewById(R.id.buscarAmigos);
        btnCerrar= (Button)findViewById(R.id.btnCerrar2);
        principal=(ImageView)findViewById(R.id.principal);

        //Crear el escuchador al clicar:
        principal.setOnClickListener(this);
        btnPersonal.setOnClickListener(this);
        btnCompartida.setOnClickListener(this);
        btnGrupal.setOnClickListener(this);
        btnCerrar.setOnClickListener(this);
        btnVerPeticionUsuario.setOnClickListener(this);
        btnBuscarAmigosUsuario.setOnClickListener(this);

        //Sharpreferens idUsuario: Recuperar usuario logueado:
        SharedPreferences preferences = getSharedPreferences("LoginUsuario", Context.MODE_PRIVATE);
        idUsuario = preferences.getInt("idUsuario", 0);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPersonal:
                //Mandar a tareas peronales
                startActivity(new Intent(this, TareasPersonales.class));
                finish();

                break;
            case R.id.btnCompartida:
                //Mandar a tareas compartidas
                startActivity(new Intent(this, ListaAmigosTareas.class));
                finish();
                break;
            case R.id.btnGrupal:
                //Mandar a tareas Grupales
                startActivity(new Intent(this, Lista_Grupos.class));
                break;
            case R.id.btnCerrar2:
                //Cerrar la sesion:
                startActivity(new Intent(this, Login_Main.class));
                finish();
                break;
            case R.id.buscarAmigos:
                //Buscar amigos para mandar peticiones
                startActivity(new Intent(this, PeticionAmigos.class));
                finish();
                break;
            case R.id.buscarPeticiones:
                //Ver si tenemos peticiones:
                startActivity(new Intent(this, VerPetiones.class));
                finish();
                break;
            case R.id.principal:
                //Volver al Login
                startActivity(new Intent(this, Login_Main.class));
                finish();
                break;
        }
    }

}
