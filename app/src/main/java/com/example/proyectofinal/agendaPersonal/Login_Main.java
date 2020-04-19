package com.example.proyectofinal.agendaPersonal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class Login_Main extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    EditText idUsername, idPassword;
    TextView linkRegistro, linkContraseña;
    String usuario, pass;
    boolean sw = false;
    int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        //Referencia a la vista:
        idUsername=(EditText)findViewById(R.id.idUsername);
        idPassword=(EditText)findViewById(R.id.idPassword);

        linkRegistro=(TextView) findViewById(R.id.linkRegistro);
        linkContraseña=(TextView) findViewById(R.id.linkContraseña);

        btnLogin=(Button)findViewById(R.id.btnLogin);

        //Escuchar el click dependiento cual pulsemos, para ello implementamos el oyente View.OnClickListener
        btnLogin.setOnClickListener(this);
        linkRegistro.setOnClickListener(this);
        linkContraseña.setOnClickListener(this);

        //Recuperamos el nombre y la contrseña del usuario para que le aparezca en el login
        //ya una vez se haya logueado y le sea mas facil:

            recuperarUsuAndPassUsuario();
    }
    @Override
    //Metodo onClick dependiendo cual cliquemos:
    public void onClick(View v) {
        switch (v.getId()){
            //Boton para mandar la información al PHP:
            case R.id.btnLogin:
                usuario=idUsername.getText().toString();
                pass=idPassword.getText().toString();
                if (!usuario.isEmpty() && !pass.isEmpty()){
                    validarUsuario("http://192.168.1.131/ProyectoNuevo/Login/loginAndroid.php");

                }else {
                    Toast.makeText(this, "No se permiten campos vacios", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.linkRegistro:
                //Nos nevia a la activity de Registro:
               startActivity(new Intent(this, Registro.class));
                break;
            //Nos envia a la activity de Oolvide Contraseña:
            case R.id.linkContraseña:
                Intent intent2 = new Intent(getApplicationContext(), com.example.proyectofinal.agendaPersonal.OlvidoPassword.class);
                startActivity(intent2);
                finish();
                break;
        }
    }

    //1ºCreamos un método con el nombre validar usuario agregándole un parámetro string destinado
    // a la ruta de nuestro servicio web:
    private void validarUsuario(String URL) {
        //2º En la siguiente línea hacemos uso de un objeto tipo StringRequest y luego dentro del constructor de la
        //clase colocamos como parámetros el tipo de método de envío (POST) la URL y seguidamente
        //agregamos la clase response listener:
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // 3º Validamos que el response no esté vacío esto dará a entender que el usuario y password
                // ingresados existen y que el servicio php nos está devolviendo la fila encontrada
                if (!response.isEmpty()){
                    try {
                        //Recogemos el JSON de PHP:
                        JSONObject jsonObject = new JSONObject(response);
                        String respuesta= jsonObject.getString("respuesta");
                        int idUsuario= jsonObject.getInt("idUsuario");

                        //4ºHacemos una serie de restricciones de logueo:

                        //A) El usuario existe por lo tanto si su rol es 1 es administrador:
                        if (respuesta.equals("1")){
                            sw = true;

                            guadarRol(respuesta);
                            //Guardamos datos para manejo de ellos en el futuro:
                            guardarUsuarioAndPasswordUsuario(idUsuario);

                            //Lanzamos a la activity de administrador:
                            Intent intent = new Intent(getApplicationContext(),Administrador.class);
                            startActivity(intent);
                            finish();

                            //B) El usuario existe por lo tanto si su rol es 2 es usuario normal:
                        }else if (respuesta.equals("2")){
                            sw = false;

                            guadarRol(respuesta);
                            //Guardamos datos para manejo de ellos en el futuro:
                            guardarUsuarioAndPasswordUsuario(idUsuario);


                            //Lanzamos a la activity de usuario:
                            Intent intent = new Intent(getApplicationContext(), Usuario.class);
                            startActivity(intent);
                            finish();

                            //C) El nombre del usuario es correcto pero la contraseña no: -2
                        }else if (respuesta.equals("noClave")){
                            Toast.makeText(Login_Main.this, "Contraseña incorrecta.", Toast.LENGTH_SHORT).show();

                            //D) El usuraio no existe en la base de datos:
                        }else if (respuesta.equals("noUsuario")){
                            Toast.makeText(Login_Main.this, "El usuario no existe.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login_Main.this,"Entra por aqui."+ error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("usuario",usuario);
                parametros.put("pass",pass);
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    //Guardar usurio para una vez logueados si no cerramos la sesión pero si la aplicación que no nos pregunte de nuevo por el loguin:
    private  void guardarUsuarioAndPasswordUsuario(int idUsuario){
        SharedPreferences preferences = getSharedPreferences("LoginUsuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("idUsuario",idUsuario);
        editor.putString("usuario",usuario);
        editor.putString("password",pass);
        editor.putBoolean("sw",sw);
        editor.commit();
    }
    //Guardemos datos de admistrador para futuras cosas:
    private  void guadarRol(String respuesta){
        SharedPreferences preferences = getSharedPreferences("idRoles", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("idRoles",respuesta);
        editor.commit();
    }
    //Aparecerá en el Login => nombre y contraseña que hayamos metido en el ultimo momento:
    private void recuperarUsuAndPassUsuario(){
        SharedPreferences preferences = getSharedPreferences("LoginUsuario", Context.MODE_PRIVATE);
        boolean sw = preferences.getBoolean("sw",false);
        if (sw){
            idUsuario = preferences.getInt("idUsuario", 0);
        }else {
            idUsername.setText(preferences.getString("usuario","Usuario1"));
            idPassword.setText(preferences.getString("password","0000"));

        }
    }
}
