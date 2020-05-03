package com.example.proyectofinal.agendaPersonal.agendaGrupal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.proyectofinal.agendaPersonal.Configuracion;
import com.example.proyectofinal.agendaPersonal.agendaCompartida.ClaseBuscarUsuarios;
import com.example.proyectofinal.agendaPersonal.agendaCompartida.Insertar_Tarea_Compartida;
import com.example.proyectofinal.agendaPersonal.agendaCompartida.ListaAmigosTareas;
import com.example.proyectofinal.agendaPersonal.agendaCompartida.Lista_tareas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerUsuariosGrupal extends AppCompatActivity {
    //Crear varibles para visualizar y las que necesitemos:
    RecyclerView idComponentes;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager manager;

    List<ClaseBuscarUsuarios> ComponentesG= new ArrayList<>();
    int idUsuario,idGrupal,idUsuario2,idUsuarioAdmin;
    String nombreUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_usuarios_grupal);
        idComponentes=(RecyclerView) findViewById(R.id.idComponentes);
        manager = new LinearLayoutManager(this);
        idComponentes.setLayoutManager(manager);

        //Sharpreferens idUsuario:
        SharedPreferences preferences = getSharedPreferences("LoginUsuario", Context.MODE_PRIVATE);
        idUsuario = preferences.getInt("idUsuario", 0);

        idGrupal= getIntent().getIntExtra("idGrupal",0);

        //Sharpreferens idUsuario:
        SharedPreferences preferences2 = getSharedPreferences("AdministradorGrupo", Context.MODE_PRIVATE);
        idUsuarioAdmin = preferences2.getInt("idUsuarioAdmind", 0);

        //De primeras que conecte con la URL de nuestro servidor PHP
        UrlAmigos("http://192.168.1.131/ProyectoNuevo/AgendaGrupal/listaComponentes.php");
    }

    //Metodo para ver los amigos que tenemos:
    public void UrlAmigos(String URL){
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){
                    try {
                        ////Recogemos el JSON y vemos si enrealidad tenemos contenido o no con la respuesta de Booleano que nos llega:
                        JSONObject jsonObject = new JSONObject(response);
                        String respuesta= jsonObject.getString("usuarios");

                            final JSONArray jsonArray = new JSONArray(respuesta);
                            //Metemos los resultados en una Arraylist que luego introduciremos en la Listview con un Array adapter:
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String nombre = object.getString("nombre");
                                idUsuario2 = object.getInt("idUsuario");
                                ComponentesG.add(new ClaseBuscarUsuarios(R.drawable.ic_uno,nombre,idUsuario2));
                            }
                            adapter = new AdaptadorVerUsuarios(getApplicationContext(),ComponentesG);

                            idComponentes.setAdapter(adapter);

                        ((AdaptadorVerUsuarios)adapter).setOnItemClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                idUsuario2= ComponentesG.get(idComponentes.getChildLayoutPosition(v)).getIdUsuario2();

                                //Dialogo solo para Administrador de Grupo:
                                if (idUsuario == idUsuarioAdmin){
                                    dialogoAdmin();
                                }else{
                                    //Otro dialogo
                                    Toast.makeText(VerUsuariosGrupal.this, "No soy administrador", Toast.LENGTH_SHORT).show();
                                    dialogoUsuario();
                                    //Enviar tarea
                                    //Salir del grupo
                                }

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Comprobacion de se la conexion es correcta entre Android y el Servidor:
                Toast.makeText(VerUsuariosGrupal.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Mandamos idUsuario para sacar todos los usuarios amigo de este:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idUsuario", String.valueOf(idUsuario));
                parametros.put("idGrupal", String.valueOf(idGrupal));
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest2);
    }
    //Mostramos un dialogo, cuando pulsamos en en uno de los usuarios:
    public void dialogoAdmin(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¿ Deseas realizar algun cambio ?").
                setIcon(R.mipmap.ic_launcher_icon_tareas);

        final CharSequence[] opciones = new CharSequence[5];
        opciones[0]="Cambiar administrador";
        opciones[1]="Añadir mas Amigos";
        opciones[2]="Enviar Tarea";
        opciones[3]="Eliminar Usuario";
        opciones[4]="Eliminar grupo";

        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                    if (opciones[which] == opciones[0]){

                        modAdmin("http://192.168.1.131/ProyectoNuevo/Administrador/AdminGrupalModAdmin7.php");
                    }else if (opciones[which] == opciones[1]){
                        Intent intent = new Intent(VerUsuariosGrupal.this, NuevoGrupo.class);
                        intent.putExtra("idGrupal",idGrupal);
                        startActivity(intent);

                    }else if (opciones[which] == opciones[2]){
                        Intent intent = new Intent(VerUsuariosGrupal.this, Insertar_Tarea_Compartida.class);// Cambiar el intem
                        intent.putExtra("idUsuario2",idUsuario2);
                        startActivity(intent);

                    }else  if (opciones[which]== opciones[3]){
                        eliminarUsuario1("http://192.168.1.131/ProyectoNuevo/AgendaGrupal/eliminarUnUsuario.php");


                    }else  if (opciones[which]== opciones[4]){
                        eliminarGrupo("http://192.168.1.131/ProyectoNuevo/Administrador/AdminGrupalEliminarG5.php");
                    }

            }
        });

        //Lanzamos el dialogo:
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void dialogoUsuario(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¿ Deseas realizar algun cambio ?").
                setIcon(R.mipmap.ic_launcher_icon_tareas);

        final CharSequence[] opciones = new CharSequence[2];
        opciones[0]="Enviar Tarea";
        opciones[1]="Salir del grupo";


        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (opciones[which] == opciones[0]){
                    Intent intent = new Intent(VerUsuariosGrupal.this, Insertar_Tarea_Compartida.class);// Cambiar el intem
                    intent.putExtra("idUsuario2",idUsuario2);
                    startActivity(intent);


                }else if (opciones[which] == opciones[1]){

                    salirGrupo("http://192.168.1.131/ProyectoNuevo/AgendaGrupal/eliminarUnUsuario.php");
                }

            }
        });

        //Lanzamos el dialogo:
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void eliminarUsuario1(String URL){
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
                            Toast.makeText(VerUsuariosGrupal.this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), VerUsuariosGrupal.class);
                            intent.putExtra("idGrupal",idGrupal);
                            startActivity(intent);
                            finish();
                            //Problema con la conexion en la base de datos:
                        }else if (respuesta.equals("0")){
                            Toast.makeText(VerUsuariosGrupal.this, "Problema en la base de datos intentelo mas tarde", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(VerUsuariosGrupal.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Le pasamor el idUsuario al PHP
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idUsuario", String.valueOf(idUsuario2));
                parametros.put("idGrupal", String.valueOf(idGrupal));
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest2);
    }
    public void eliminarGrupo(String URL){
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
                            Toast.makeText(VerUsuariosGrupal.this, "El grupo fue eliminado", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Lista_Grupos.class);
                            intent.putExtra("idGrupal",idGrupal);
                            startActivity(intent);
                            finish();
                            //Problema con la conexion en la base de datos:
                        }else if (respuesta.equals("0")){
                            Toast.makeText(VerUsuariosGrupal.this, "Problema en la base de datos intentelo mas tarde", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(VerUsuariosGrupal.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Le pasamor el idUsuario al PHP
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idGrupal", String.valueOf(idGrupal));
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest2);
    }
    public void salirGrupo(String URL){
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
                            Toast.makeText(VerUsuariosGrupal.this, "Te has ido del grupo", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Lista_Grupos.class);
                            intent.putExtra("idGrupal",idGrupal);
                            startActivity(intent);
                            finish();
                            //Problema con la conexion en la base de datos:
                        }else if (respuesta.equals("0")){
                            Toast.makeText(VerUsuariosGrupal.this, "Problema en la base de datos intentelo mas tarde", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(VerUsuariosGrupal.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Le pasamor el idUsuario al PHP
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idUsuario", String.valueOf(idUsuario));
                parametros.put("idGrupal", String.valueOf(idGrupal));
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest2);

    }
    public void modAdmin(String URL){
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){
                    try {
                        //Recogemos respuesta del JSON
                        JSONObject jsonObject = new JSONObject(response);
                        String respuesta= jsonObject.getString("res");

                        //Si la respuesta es 1, el usuario se elimina:
                        if (respuesta.equals("1")){
                            Toast.makeText(VerUsuariosGrupal.this, "Ahora el admin es: "+nombreUsuario, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), VerUsuariosGrupal.class);

                            guardarAdmin(idUsuario2);

                            intent.putExtra("idGrupal",idGrupal);
                            intent.putExtra("idUsuarioAdmind",idUsuarioAdmin);
                            startActivity(intent);
                            finish();
                            //Problema con la conexion en la base de datos:
                        }else if (respuesta.equals("0")){
                            Toast.makeText(VerUsuariosGrupal.this, "Problema en la base de datos intentelo mas tarde", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(VerUsuariosGrupal.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Le pasamor el idUsuario al PHP
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idAdmin", String.valueOf(idUsuario2));
                parametros.put("idGrupal", String.valueOf(idGrupal));
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest2);


    }
    //Guardemos datos de admistrador de Grupos para futuras cosas:
    public   void guardarAdmin(int idUsuarioAdmin){
        SharedPreferences preferences = getSharedPreferences("AdministradorGrupo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("idUsuarioAdmind",idUsuarioAdmin);
        editor.commit();
    }
    public void volverA(View view){
        Intent intent = new Intent(VerUsuariosGrupal.this, Lista_Tareas_Grupal.class);//Crear Ver usuarios
        intent.putExtra("idGrupal",idGrupal);
        startActivity(intent);
    }
}
