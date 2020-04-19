package com.example.proyectofinal.agendaPersonal;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.proyectofinal.agendaPersonal.agendaCompartida.ListaAmigosTareas;
import com.example.proyectofinal.agendaPersonal.agendaCompartida.VerPetiones;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Configuracion extends AppCompatActivity  {
    //Crear varibles para visualizar y las que necesitemos:
    ImageView volverAdmind;
    Button insertarUsu;
    ArrayList<String> listaUsuarios = new ArrayList<>();
    ArrayList<String> prueba = new ArrayList<>();
    ListView listViewUsuarios;
    TextView tituloUsuarios;
    EditText buscarUsuarios;
    int idUsuario, idUsuario2;
    String rol, nombre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        volverAdmind=(ImageView)findViewById(R.id.volverAdmin);
        listViewUsuarios= (ListView)findViewById(R.id.listaUsuarios);
        tituloUsuarios = (TextView)findViewById(R.id.tituloUsuarios);
        buscarUsuarios = (EditText)findViewById(R.id.editBuscarUsuarios);

        //Sharpreferens idUsuario:
        SharedPreferences preferences = getSharedPreferences("LoginUsuario", Context.MODE_PRIVATE);
        idUsuario = preferences.getInt("idUsuario", 0);

        //De primeras que conecte con la URL de nuestro servidor PHP todos los usuarios:
        UrlListaUsuarios("http://192.168.1.131/ProyectoNuevo/AgendaCompartida/todosUsuarios.php");

    }
    public void UrlListaUsuarios(String URL){
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){
                    try {
                        ////Recogemos el JSON y vemos si enrealidad tenemos contenido o no con la respuesta de Booleano que nos llega:
                        JSONObject jsonObject = new JSONObject(response);
                        String respuesta= jsonObject.getString("usuarios");
                        int booleano= jsonObject.getInt("booleano");

                        if (booleano == 1){
                            //Si el booleano es 1 se muestra este testo un textview:
                            tituloUsuarios.setText("Buscar Usuarios por nombre:");
                            final JSONArray jsonArray = new JSONArray(respuesta);
                            //Metemos los resultados en una Arraylist que luego introduciremos en la Listview con un Array adapter:
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                //Recogemos loe nombres de los usuarios:
                                String nombre = object.getString("nombre");
                                listaUsuarios.add(nombre);
                            }
                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(Configuracion.this, android.R.layout.simple_list_item_1, listaUsuarios);
                            listViewUsuarios.setAdapter(adapter);
                            listViewUsuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                               @Override
                               public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                   try {
                                       idUsuario2 = Integer.parseInt(jsonArray.getJSONObject(position).getString("idUsuario"));
                                       prueba.add(String.valueOf(idUsuario2));


                                       Toast.makeText(Configuracion.this, "Pulsado: "+prueba, Toast.LENGTH_SHORT).show();
                                       muestraDialogo(jsonArray,position);

                                   } catch (JSONException e) {
                                       e.printStackTrace();
                                   }

                               }
                           });
                            buscarUsuarios.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    adapter.getFilter().filter(s);
                                }

                                @Override
                                public void afterTextChanged(Editable s) {


                                }
                            });


                            //Utilizamos un buscador por si estamos buscando alguno en concreto:

                        //Si no hay usuarios se mostrara el siguiente mensaje:
                        }else if (booleano == 0 && respuesta.equals("0")){
                            tituloUsuarios.setText("No hay usuarios haz alguno");
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
                Toast.makeText(Configuracion.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Mandamos el idUsuario al PHP para sacar todos que los que no sean este:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idUsuario", String.valueOf(idUsuario));
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest2);
    }

    //Mostramos un dialogo, cuando pulsamos en en uno de los usuarios:
    public void muestraDialogo(final JSONArray jsonArray, final int posicion){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿ Deseas realizar algun cambio ?")
                //Esliminar que lo elimina de la tabla de usuarios y borra las relaciones que tubiera en la tabla de amisgos:
                .setNeutralButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                            //llamamos al metodo eliminar de PHP
                            eliminarUsuario("http://192.168.1.131/ProyectoNuevo/Administrador/eliminarUsuario.php");


                    }
                })
                //Administrador, comvierte al usuario en admin:
                .setPositiveButton("Administardor", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                            ///Llamamos al cambio de usuario que conecta con el PHP modificarAdmin y le pasamos el rol 1
                            rol="1";
                            cambioUsuarios("http://192.168.1.131/ProyectoNuevo/Administrador/modificarAdmin.php");

                    }
                })
                //Usuario , convierte al administrador en usuario:
                .setNegativeButton("Usuario", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                            //Llamamos al metodo cambio de usuario de nuevo pero esta vez le pasamos el rol = 2
                            rol="2";
                            cambioUsuarios("http://192.168.1.131/ProyectoNuevo/Administrador/modificarAdmin.php");

                    }
                });
        //Lanzamos el dialogo:
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void cambioUsuarios(String URL){
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){
                    try {
                        ////Recogemos el JSON
                        JSONObject jsonObject = new JSONObject(response);
                        String respuesta= jsonObject.getString("respuesta");

                        //Si respuesta es igual a uno se produce el cambio, PHP lo cambia segun el rol enviado:
                        if (respuesta.equals("1")){
                            //Para comprobarlo en android hacemos este if que dice si rol es 1 es admin, si rol es 2 es ususario:
                            if (rol.equals("1")){
                                Toast.makeText(Configuracion.this, "Cambio realizado ha admin", Toast.LENGTH_SHORT).show();
                            }else if (rol.equals("2")){
                                Toast.makeText(Configuracion.this, "Cambio realizado ha usuario", Toast.LENGTH_SHORT).show();
                            }
                            //Si la respuesta es cero, hay un problema de conexion con la base de datos:
                        }else if (respuesta.equals("0")){
                            Toast.makeText(Configuracion.this, "Problema en la base de datos intentelo mas tarde", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Configuracion.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Le pasamos al PHP el idUsuario y el rol para que realice los cambios:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idUsuario", String.valueOf(idUsuario2));
                parametros.put("rol", String.valueOf(rol));
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest2);
    }
    public void eliminarUsuario(String URL){
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){
                    try {
                        //Recogemos respuesta del JSON
                        JSONObject jsonObject = new JSONObject(response);
                        String respuesta= jsonObject.getString("respuesta");

                        //Si la respuesta es 1, el usuario se elimina:
                        if (respuesta.equals("1")){
                            Toast.makeText(Configuracion.this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Configuracion.class);
                            intent.putExtra("idUsuario",idUsuario);
                            startActivity(intent);
                            finish();
                            //Problema con la conexion en la base de datos:
                        }else if (respuesta.equals("0")){
                            Toast.makeText(Configuracion.this, "Problema en la base de datos intentelo mas tarde", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Configuracion.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Le pasamor el idUsuario al PHP
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idUsuario", String.valueOf(idUsuario2));
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest2);
    }
    //Atras de nuevo
    public void  Vadmin(View view){
        Intent intent = new Intent(getApplicationContext(), Administrador.class);
        intent.putExtra("idUsuario",idUsuario2);
        intent.putExtra("rol",rol);
        startActivity(intent);
        finish();
    }
    //boton insertar usuarios que me lleba a la vista:
    public void Insert(View view){
        Intent intent2 = new Intent(getApplicationContext(), InsertarUsuarios.class);
        intent2.putExtra("idUsuario",idUsuario2);
        intent2.putExtra("rol",rol);
        startActivity(intent2);
        finish();
    }
}
