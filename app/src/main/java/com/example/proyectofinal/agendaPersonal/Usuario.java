package com.example.proyectofinal.agendaPersonal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;

import com.example.proyectofinal.R;
import com.example.proyectofinal.agendaPersonal.agendaCompartida.ListaAmigosTareas;
import com.example.proyectofinal.agendaPersonal.agendaCompartida.PeticionAmigos;
import com.example.proyectofinal.agendaPersonal.agendaCompartida.VerPetiones;
import com.example.proyectofinal.agendaPersonal.agendaGrupal.Lista_Grupos;


public class Usuario extends AppCompatActivity implements View.OnClickListener {
    //Crear varibles para visualizar y las que necesitemos:
    CardView btnPersonal;
    CardView btnCompartida;
    CardView btnGrupal;
    Button btnCerrar;
    int idUsuario;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);

        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        btnPersonal= (CardView) findViewById(R.id.btnPersonal);
        btnCompartida= (CardView) findViewById(R.id.btnCompartida);
        btnGrupal= (CardView) findViewById(R.id.btnGrupal);
        btnCerrar= (Button)findViewById(R.id.btnCerrar2);


        //Crear el escuchador al clicar:

        btnPersonal.setOnClickListener(this);
        btnCompartida.setOnClickListener(this);
        btnGrupal.setOnClickListener(this);
        btnCerrar.setOnClickListener(this);


        //Sharpreferens idUsuario: Recuperar usuario logueado:
        SharedPreferences preferences = getSharedPreferences("LoginUsuario", Context.MODE_PRIVATE);
        idUsuario = preferences.getInt("idUsuario", 0);

    }
    //Metodo para mostrar menu oculto:
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.item1){

            //Buscar amigos para mandar peticiones
            startActivity(new Intent(this, PeticionAmigos.class));
            finish();
        }else if (id == R.id.item2){

            //Ver si tenemos peticiones:
            startActivity(new Intent(this, VerPetiones.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
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

        }
    }

}
