package com.example.proyectofinal.agendaPersonal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyectofinal.R;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity implements View.OnClickListener {

    Button btnRegristro;
    EditText idNombre, idPassword, idPassword2, idCorreo;
    String nombre, pass, pass2, email;
    ImageView volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        btnRegristro= (Button)findViewById(R.id.btnRegistro);
        idNombre= (EditText)findViewById(R.id.idNombre);
        idPassword= (EditText)findViewById(R.id.idPassword);
        idPassword2= (EditText)findViewById(R.id.idPassword2);
        idCorreo= (EditText)findViewById(R.id.email);
        volver=(ImageView)findViewById(R.id.Vlogin);

        btnRegristro.setOnClickListener(this);
        volver.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnRegistro:
                nombre= idNombre.getText().toString();
                pass= idPassword.getText().toString();
                pass2= idPassword2.getText().toString();
                email = idCorreo.getText().toString();

                if (!nombre.isEmpty() && !pass.isEmpty() && !pass2.isEmpty() && !email.isEmpty()){
                    insertarUsurario("http://192.168.1.131/ProyectoNuevo/Registro/registroAndroid.php");
                }else {
                    Toast.makeText(this, "No se permiten campos vacios", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.Vlogin:
                startActivity(new Intent(this, Login_Main.class));
                break;

        }

    }
    public void insertarUsurario(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()) {

                    if (response.equals("<return>usunuevo</return>")) {
                        Toast.makeText(Registro.this, "Registro completado con exito", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),Login_Main.class);
                        startActivity(intent);
                        finish();
                    } else if (response.equals("<return>usunuevo</return><return>false</return>")){
                        Toast.makeText(Registro.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    }else if (response.equals("<return>existe</return>")){
                        Toast.makeText(Registro.this, "El usuario ya esta existe", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Registro.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("nombre",nombre);
                parametros.put("pass",pass);
                parametros.put("pass2",pass2);
                parametros.put("email", email);
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}
