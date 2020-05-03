package com.example.proyectofinal.agendaPersonal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    Button btnCerrarAdmin;

    CardView btnPersonalAdmin;
    CardView btnCompartidaAdmin;
    CardView btnGrupalAdmin;
    int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);

        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        btnPersonalAdmin= (CardView) findViewById(R.id.btnPersonalAdmin);
        btnCompartidaAdmin= (CardView) findViewById(R.id.btnCompartidaAdmin);
        btnGrupalAdmin= (CardView) findViewById(R.id.btnGrupalAdmin);
        btnCerrarAdmin= (Button)findViewById(R.id.btnCerrarAdmin);

        btnPersonalAdmin.setOnClickListener(this);
        btnCompartidaAdmin.setOnClickListener(this);
        btnGrupalAdmin.setOnClickListener(this);
        btnCerrarAdmin.setOnClickListener(this);


        //Sharpreferens idUsuario:
        SharedPreferences preferences = getSharedPreferences("LoginUsuario", Context.MODE_PRIVATE);
        idUsuario = preferences.getInt("idUsuario", 0);

    }

    //Metodo para mostrar menu oculto:
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item2){
        int id = item2.getItemId();

        if (id == R.id.item3){

            //Buscar amigos para mandar peticiones
            startActivity(new Intent(this, PeticionAmigos.class));
            finish();
        }else if (id == R.id.item4){

            //Ver si tenemos peticiones:
            startActivity(new Intent(this, VerPetiones.class));
            finish();
        }else if (id == R.id.item5){

            //Configuración:
            startActivity(new Intent(this, Configuracion.class));
            finish();
        }
        return super.onOptionsItemSelected(item2);
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

        }
    }
}
