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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
public class InsertarUsuarios extends AppCompatActivity implements View.OnClickListener {
    //Crear varibles para visualizar y las que necesitemos:
    Button btnInsertar;
    EditText idNombre, idPassword, idCorreo;
    private String nombre, pass, email;
    ImageView volverConfi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar_usuarios);
        volverConfi=(ImageView)findViewById(R.id.volverConfi);
        btnInsertar=(Button)findViewById(R.id.btnInsert);
        idNombre=(EditText)findViewById(R.id.idNombreUsuarios);
        idCorreo=(EditText)findViewById(R.id.idEmails);
        idPassword=(EditText)findViewById(R.id.idContra);

        //Escuchar el click dependiento cual pulsemos, para ello implementamos el oyente View.OnClickListener:
        btnInsertar.setOnClickListener(this);
        volverConfi.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        //Switch para hacer la diferencia entre pulsar un boton u otro:
        switch (v.getId()){
            //Boton para guardar lo escrito en los EditText:
            case R.id.btnInsert:
                nombre= idNombre.getText().toString();
                pass= idPassword.getText().toString();
                email = idCorreo.getText().toString();

                //Si los campos no estan vacios conectamos al PHP:
                if (!nombre.isEmpty() && !pass.isEmpty() && !email.isEmpty()){
                    URLusuariosNuevos("http://192.168.1.131/ProyectoNuevo/Administrador/crearUsuarios.php?");
                }else {
                    Toast.makeText(this, "No se permiten campos vacios", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.volverConfi:
                //Boton para regresar a cofiguracion:
                startActivity(new Intent(this, Configuracion.class));
                break;
        }
    }
    //Metodo que conecta con el PHP:
    public void URLusuariosNuevos(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Leemos la respuesta que nos ofrece el PHP
                if (!response.isEmpty()) {
                    //Recogemos el JSON de PHP:
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String respuesta= jsonObject.getString("respuesta");
                        //Si el usuario ya existe nos lanza un toast(diciendo que ya existe) y no, nos deja insertarlo:
                        if (respuesta.equals("1")) {
                            Toast.makeText(InsertarUsuarios.this, "El usuario ya existe, prueba con otro.", Toast.LENGTH_SHORT).show();

                        //Si es 0 la respuesta nos deja insertarlo: y nos manda a configuracion de nuevo para verificarlo:
                        } else if (respuesta.equals("0")){
                            Toast.makeText(InsertarUsuarios.this, "Insertado con exito.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),Configuracion.class);
                            startActivity(intent);
                            finish();
                        //Si da un 2 fallo en la base de datos:
                        }else if (response.equals("2")){
                            Toast.makeText(InsertarUsuarios.this, "Error en la base de datos, intentelo mas tarde", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            //Si no se puede conectar al php nos manda el mensaje en un Toast: El sitio web no esta en servicio intentelo mas tarde.
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(InsertarUsuarios.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }
        }){
            //Aqui le enviamos por medio de Post los parametros que necesita el PHP:
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("nombre",nombre);
                parametros.put("pass",pass);
                parametros.put("email", email);
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
