package com.example.proyectofinal.agendaPersonal.agendaGrupal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import com.example.proyectofinal.agendaPersonal.agendaCompartida.ListaAmigosTareas;
import com.example.proyectofinal.agendaPersonal.agendaCompartida.Lista_tareas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VerUsuariosGrupal extends AppCompatActivity {
    //Crear varibles para visualizar y las que necesitemos:
    ListView idComponentes;
    ArrayList<String> ComponentesG= new ArrayList<>();
    int idUsuario,idGrupal,idUsuario2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_usuarios_grupal);
        idComponentes=(ListView)findViewById(R.id.idComponentes);

        //Sharpreferens idUsuario:
        SharedPreferences preferences = getSharedPreferences("LoginUsuario", Context.MODE_PRIVATE);
        idUsuario = preferences.getInt("idUsuario", 0);

        idGrupal= getIntent().getIntExtra("idGrupal",0);

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
                            int cont=0;
                            //Metemos los resultados en una Arraylist que luego introduciremos en la Listview con un Array adapter:
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                cont++;
                                JSONObject object = jsonArray.getJSONObject(i);
                                String finalstring = cont+"."+object.getString("nombre");
                                ComponentesG.add(finalstring);
                            }
                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(VerUsuariosGrupal.this, android.R.layout.simple_spinner_item, ComponentesG);
                            idComponentes.setAdapter(adapter);
                            idComponentes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    try{
                                        //Al pulsar en la lista guardamos el idUsurio que nos a enviado el JSON
                                        idUsuario2 = Integer.parseInt(jsonArray.getJSONObject(position).getString("idUsuario"));
                                        Toast.makeText(VerUsuariosGrupal.this, "Pulsado: "+idUsuario2, Toast.LENGTH_SHORT).show();

                                        muestraDialogo(jsonArray,position);

                                    }catch (JSONException e) {
                                        e.printStackTrace();
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


    public void volverTaGrupal(View view){

    }
    //Mostramos un dialogo, cuando pulsamos en en uno de los usuarios:
    public void muestraDialogo(final JSONArray jsonArray, final int posicion){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿ Deseas realizar algun cambio ?")
                //Esliminar que lo elimina de la tabla de usuarios y borra las relaciones que tubiera en la tabla de amisgos:
                .setNeutralButton("Eliminar Usuario", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        eliminarUsuario1("http://192.168.1.131/ProyectoNuevo/AgendaGrupal/eliminarUnUsuario.php");

                        //CONTINUARA....Esta parte es del administrador de grupo que yo no puse en la propuesta
                        //Con lo cual es a mayores...
                    }
                });
                /* Administrador, comvierte al usuario en admin:
                .setPositiveButton("Administardor", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        cambioUsuarios("http://192.168.1.131/ProyectoNuevo/Administrador/modificarAdmin.php");

                    }
                })
                //Usuario , convierte al administrador en usuario:
                .setNegativeButton("Usuario", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        cambioUsuarios("http://192.168.1.131/ProyectoNuevo/Administrador/modificarAdmin.php");

                    }
                });*/
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
}
