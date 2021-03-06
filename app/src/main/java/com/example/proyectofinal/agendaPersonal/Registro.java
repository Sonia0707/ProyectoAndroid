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

    //Crear varibles para visualizar y las que necesitemos:
    Button btnRegristro;
    EditText idNombre, idPassword, idPassword2, idCorreo;
    private String nombre, pass, pass2, email;
    ImageView volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //Visualizamos los controles de la vista:

        btnRegristro= (Button)findViewById(R.id.btnRegistro);
        idNombre= (EditText)findViewById(R.id.idNombre);
        idPassword= (EditText)findViewById(R.id.idPassword);
        idPassword2= (EditText)findViewById(R.id.idPassword2);
        idCorreo= (EditText)findViewById(R.id.email);
        volver=(ImageView)findViewById(R.id.Vlogin);

        //Escuchar el click dependiento cual pulsemos, para ello implementamos el oyente View.OnClickListener:
        btnRegristro.setOnClickListener(this);
        volver.setOnClickListener(this);
    }
    //Metodo onClick dependiendo cual cliquemos:
    @Override
    public void onClick(View v) {
        //Switch para hacer la diferencia entre pulsar un boton u otro:
        switch (v.getId()){
            //Boton para guardar lo escrito en los EditText:
            case R.id.btnRegistro:
                nombre= idNombre.getText().toString();
                pass= idPassword.getText().toString();
                pass2= idPassword2.getText().toString();
                email = idCorreo.getText().toString();

                //Si los campos no estan vacios conectamos al PHP:
                if (!nombre.isEmpty() && !pass.isEmpty() && !pass2.isEmpty() && !email.isEmpty()){
                    insertarUsurario("http://192.168.1.131/ProyectoNuevo/Registro/registroAndroid.php");
                }else {
                    Toast.makeText(this, "No se permiten campos vacios", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.Vlogin:
                //Boton para regresar al LOGIN:
                startActivity(new Intent(this, Login_Main.class));
                break;

        }

    }
    //Metodo que conecta con el PHP:
    public void insertarUsurario(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Leemos la respuesta que nos ofrece el PHP en Forma de XML:
                if (!response.isEmpty()) {
                        //Si todo es correcto nos hace el registro en la base de datos y nos lo manda al LOGIN de nuevo:
                    if (response.equals("<return>usunuevo</return>")) {
                        Toast.makeText(Registro.this, "Registro completado con exito", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),Login_Main.class);
                        startActivity(intent);
                        finish();
                        //Si la segunda contraseña no coincide nos lanza un mensaje en el Toast diciendo que no coinciden:
                    } else if (response.equals("<return>usunuevo</return><return>false</return>")){
                        Toast.makeText(Registro.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                        //Si el usuario ya existe nos manda un mensaje en un Toast diciendo que ya existe:
                    }else if (response.equals("<return>existe</return>")){
                        Toast.makeText(Registro.this, "El usuario ya esta existe", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        }, new Response.ErrorListener() {
            //Si no se puede conectar al php nos manda el mensaje en un Toast: El sitio web no esta en servicio intentelo mas tarde.
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Registro.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();

            }
        }){
            //Aqui le enviamos por medio de Post los parametros que necesita el PHP:
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
