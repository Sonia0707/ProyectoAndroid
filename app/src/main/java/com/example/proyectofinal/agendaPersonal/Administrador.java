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

public class Administrador extends AppCompatActivity implements View.OnClickListener {

    ImageView btnPersonalAdmin;
    ImageView btnCompartidaAdmin;
    ImageView btnGrupalAdmin;
    ImageView principalAdmin;
    Button btnCerrarAdmin;
    FloatingActionButton btnConfiguracion, btnVerPeticion,btnBuscarAmigos;
    int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);
        btnPersonalAdmin= (ImageView) findViewById(R.id.btnPersonalAdmin);
        btnCompartidaAdmin= (ImageView) findViewById(R.id.btnCompartidaAdmin);
        btnGrupalAdmin= (ImageView) findViewById(R.id.btnGrupalAdmin);
        btnCerrarAdmin= (Button)findViewById(R.id.btnCerrarAdmin);
        btnConfiguracion= (FloatingActionButton) findViewById(R.id.btnConfiguracion);
        btnVerPeticion= (FloatingActionButton) findViewById(R.id.buscarPeticionesAdmin);
        btnBuscarAmigos= (FloatingActionButton) findViewById(R.id.buscarAmigosAdmin);
        principalAdmin=(ImageView)findViewById(R.id.idvolverLogin);

        principalAdmin.setOnClickListener(this);
        btnPersonalAdmin.setOnClickListener(this);
        btnCompartidaAdmin.setOnClickListener(this);
        btnGrupalAdmin.setOnClickListener(this);
        btnCerrarAdmin.setOnClickListener(this);
        btnConfiguracion.setOnClickListener(this);
        btnBuscarAmigos.setOnClickListener(this);
        btnVerPeticion.setOnClickListener(this);

        //Sharpreferens idUsuario:
        SharedPreferences preferences = getSharedPreferences("LoginUsuario", Context.MODE_PRIVATE);
        idUsuario = preferences.getInt("idUsuario", 0);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPersonalAdmin:
                //startActivity(new Intent(this, AGpersonal.class));
                startActivity(new Intent(this, TareasPersonales.class));
                finish();

                break;
            case R.id.btnCompartidaAdmin:

                startActivity(new Intent(this, ListaAmigosTareas.class));
                finish();

                break;
            case R.id.btnGrupalAdmin:
                startActivity(new Intent(this, Lista_Grupos.class));
                break;
            case R.id.btnCerrarAdmin:
                startActivity(new Intent(this, Login_Main.class));
                finish();
                break;
            case R.id.btnConfiguracion:
                Intent intent4 = new Intent(getApplicationContext(),Configuracion.class);
                startActivity(intent4);
                finish();
                break;
            case R.id.buscarPeticionesAdmin:
                startActivity(new Intent(this, VerPetiones.class));
                finish();
                break;
            case R.id.buscarAmigosAdmin:
                startActivity(new Intent(this, PeticionAmigos.class));
                finish();
                break;

            case R.id.idvolverLogin:
                startActivity(new Intent(this, Login_Main.class));
                finish();
                break;

        }
    }
}
