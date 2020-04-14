package com.example.proyectofinal.agendaPersonal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.proyectofinal.R;
import com.example.proyectofinal.agendaPersonal.agendaCompartida.ListaAmigosTareas;

public class Usuario extends AppCompatActivity implements View.OnClickListener {

    ImageView btnPersonal;
    ImageView btnCompartida;
    ImageView btnGrupal;
    ImageView principal;
    Button btnCerrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        btnPersonal= (ImageView) findViewById(R.id.btnPersonal);
        btnCompartida= (ImageView) findViewById(R.id.btnCompartida);
        btnGrupal= (ImageView) findViewById(R.id.btnGrupal);
        btnCerrar= (Button)findViewById(R.id.btnCerrar2);
        principal=(ImageView)findViewById(R.id.principal);

        principal.setOnClickListener(this);
        btnPersonal.setOnClickListener(this);
        btnCompartida.setOnClickListener(this);
        btnGrupal.setOnClickListener(this);
        btnCerrar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPersonal:
                //startActivity(new Intent(this, AGpersonal.class));
                Intent intent1 = new Intent(getApplicationContext(),TareasPersonales.class);
                startActivity(intent1);
                finish();

                break;
            case R.id.btnCompartida:

                startActivity(new Intent(this, ListaAmigosTareas.class));
                finish();

                break;
            case R.id.btnGrupal:
                //startActivity(new Intent(this, AGcompartida.class));
                break;
            case R.id.btnCerrar2:

                Intent intent4 = new Intent(getApplicationContext(),Login_Main.class);
                startActivity(intent4);
                finish();
                break;
            case R.id.principal:
                Intent intent5 = new Intent(getApplicationContext(),Login_Main.class);
                startActivity(intent5);
                finish();
                break;

        }
    }

}
