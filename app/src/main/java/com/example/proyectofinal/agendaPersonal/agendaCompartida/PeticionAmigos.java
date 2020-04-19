package com.example.proyectofinal.agendaPersonal.agendaCompartida;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
import com.example.proyectofinal.agendaPersonal.Administrador;
import com.example.proyectofinal.agendaPersonal.Login_Main;
import com.example.proyectofinal.agendaPersonal.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class PeticionAmigos extends AppCompatActivity {
    //Crear varibles para visualizar y las que necesitemos:
    ArrayList<String> listUsuarios = new ArrayList<>();
    ListView listViewUsuarios;
    TextView noUsuarios;
    EditText buscarUsuarios;
    int idUsuario,idUsuario2;
    String idRoles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peticion_amigos);
        listViewUsuarios=(ListView)findViewById(R.id.peticiones);
        buscarUsuarios=(EditText) findViewById(R.id.editBuscar);
        noUsuarios=(TextView)findViewById(R.id.noUsuarios);

        //Sharpreferens idUsuario:
        SharedPreferences preferences = getSharedPreferences("LoginUsuario", Context.MODE_PRIVATE);
        idUsuario = preferences.getInt("idUsuario", 0);

        //Recogemos el rol del Login para comprobar si es usuario o administrador y dependiendo de eso se manda a una actiivity u otra:
        SharedPreferences preferences2 = getSharedPreferences("idRoles", Context.MODE_PRIVATE);
        idRoles=preferences2.getString("idRoles","");

        //De primeras que conecte con la URL de nuestro servidor PHP
        buscarUsuariosList("http://192.168.1.131/ProyectoNuevo/AgendaCompartida/todosUsuarios.php");
    }
    //Metodo para Buscar todos los Usuarios y poder realizar petidiones:
    public void buscarUsuariosList(String URL){
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            public void onResponse(String response) {

                //Validamos que el response si no falla nos da a entender que la conexión es buena
                if (!response.isEmpty()){
                    try {
                        //Recogemos el JSON y vemos si enrealidad tenemos contenido o no con la respuesta de Booleano que nos llega:
                        JSONObject jsonObject = new JSONObject(response);
                        String respuesta= jsonObject.getString("usuarios");
                        int booleano= jsonObject.getInt("booleano");

                        if (booleano == 1){
                            //Si el booleano es 1 se muestra este testo un textview:
                            noUsuarios.setText("Lista de Usuarios:");
                            final JSONArray jsonArray = new JSONArray(respuesta);
                            //Metemos los resultados en una Arraylist que luego introduciremos en la Listview con un Array adapter:
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                //Recogemos loe nombres:
                                String finalstring = object.getString("nombre");
                                listUsuarios.add(finalstring);
                            }
                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(PeticionAmigos.this, android.R.layout.simple_spinner_item, listUsuarios);
                            listViewUsuarios.setAdapter(adapter);
                            listViewUsuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    try{
                                        //Pasamoes Recogemos el idUsuario2 para hacer la peticion:
                                        idUsuario2 = Integer.parseInt(jsonArray.getJSONObject(position).getString("idUsuario"));
                                        Toast.makeText(PeticionAmigos.this, "Pulsado: "+idUsuario2, Toast.LENGTH_SHORT).show();
                                       // peticionAmigos("http://192.168.1.131/ProyectoNuevo/AgendaCompartida/peticion.php?");
                                    }catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            //Un buscador por si hay muchos usuarios que sea mas facil encontrar a nuestro amigo:
                            buscarUsuarios.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged( CharSequence s, int start, int before, int count) {
                                    adapter.getFilter().filter(s);
                                }

                                @Override
                                public void afterTextChanged(final Editable s) {

                                }
                            });

                        //Si no hubiera ningún usuario:
                        }else if (booleano == 0 && respuesta.equals("0")){
                            noUsuarios.setText("No hay usuarios");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Comprobacion de se la conexion es correcta entre Android y el Servidor:
                Toast.makeText(PeticionAmigos.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Mandamos el idUsuario para que salgan todos los usuarios menos nosotros:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idUsuario", String.valueOf(idUsuario));
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest2);
    }
    //Metodo petición de amigos que este ya con los dos usuarios nos hace la peticion:
    public void peticionAmigos(String URL){
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String respuesta= jsonObject.getString("respuesta");

                        //4ºHacemos una serie de restricciones en las peticiones:

                        //A) El usuario ya ha enviado la peticion por lo tanto no puede volver a mandarla:
                        if (respuesta.equals("2")){

                            Toast.makeText(PeticionAmigos.this, "Ya has echo petición de amistad", Toast.LENGTH_SHORT).show();

                            //B) Ya son amigo por lo tanto no le puede mandar ninguna petición:
                        }else if (respuesta.equals("1")){

                            Toast.makeText(PeticionAmigos.this, "Ya sois amigos", Toast.LENGTH_SHORT).show();

                            //C) Peticion de amistad mandada esperando respuesta:
                        }else if (respuesta.equals("0")){
                            Toast.makeText(PeticionAmigos.this, "Petición mandada esperando respuesta...", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ListaAmigosTareas.class);
                            startActivity(intent);
                            finish();

                            //D) Problemas con la base de datos:
                        }else if (respuesta.equals("3")){
                            Toast.makeText(PeticionAmigos.this, "Problema en la base de datos intentelo mas tarde", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ListaAmigosTareas.class);
                            startActivity(intent);
                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Comprobacion de se la conexion es correcta entre Android y el Servidor:
                Toast.makeText(PeticionAmigos.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Mandamos al PHP los dos usuarios y hacemos petición:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idUsuario2", String.valueOf(idUsuario2));
                parametros.put("idUsuario", String.valueOf(idUsuario));
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest2);
    }
    //Volver atras:
    public void volverListAmigos2(View view){
        if (idRoles.equals("1")){
            startActivity(new Intent(this, Administrador.class));
            finish();
        }else if (idRoles.equals("2")){
            startActivity(new Intent(this, Usuario.class));
            finish();
        }
    }
}

