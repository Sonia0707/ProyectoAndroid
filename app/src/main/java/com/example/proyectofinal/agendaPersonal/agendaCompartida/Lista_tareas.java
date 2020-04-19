package com.example.proyectofinal.agendaPersonal.agendaCompartida;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lista_tareas extends AppCompatActivity {
    //Crear varibles para visualizar y las que necesitemos:
    ListView idFechasTitulo;
    String fecha,titulo,descrip,hora,nombre;
    TextView textoFechas;
    ArrayList<String> listFechasTitulo= new ArrayList<>();
    int idUsuario,idUsuario2, idComp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tareas);
        idFechasTitulo=(ListView)findViewById(R.id.idFechasTitulo);
        textoFechas=(TextView)findViewById(R.id.textoFechas);

        //Sharpreferens idUsuario:
        SharedPreferences preferences = getSharedPreferences("LoginUsuario", Context.MODE_PRIVATE);
        idUsuario = preferences.getInt("idUsuario", 0);

        //Recuperamos el idUsuario2 de la otra actividad al clicar en el amigo:
        idUsuario2= getIntent().getIntExtra("idUsuario2",0);

        //De primeras que conecte con la URL de nuestro servidor PHP:
        UrlFechas("http://192.168.1.131/ProyectoNuevo/AgendaCompartida/fechasTitulo.php");
    }
    //Metodo para sacar las fechas y titulos de nuestras tareas compartidas:
    public void UrlFechas(String URL){
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){

                    try {
                        //Respuesta del JSON del PHP:
                        JSONObject jsonObject = new JSONObject(response);
                        String respuesta= jsonObject.getString("fechas");
                        int booleano= jsonObject.getInt("booleano");

                        if (booleano == 1){
                            //Si el booleano es 1 se ve esto en el texview:
                            textoFechas.setText("Lista de Tareas:");
                            final JSONArray jsonArray = new JSONArray(respuesta);
                            //Metemos los resultados en una Arraylist que luego introduciremos en la Listview con un Array adapter:
                            int cont=0;
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                cont++;
                                JSONObject object = jsonArray.getJSONObject(i);
                                //Sacamos todo el contenido pero para esta vista solo mostrasmos el titulo de la tarea y la fecha:
                                String finalstring = cont+"."+object.getString("titulo")+" -> "+object.getString("fecha");
                                listFechasTitulo.add(finalstring);
                            }

                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(Lista_tareas.this, android.R.layout.simple_spinner_item, listFechasTitulo);
                            idFechasTitulo.setAdapter(adapter);
                            idFechasTitulo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    try{//El contenido siguiente lo guardamos para mandarselo a la siguiente vista donde veremos
                                        //la tarea clicada al pasarle el idComp unico:
                                        idUsuario2 = Integer.parseInt(jsonArray.getJSONObject(position).getString("idUsuario"));
                                        idComp = Integer.parseInt(jsonArray.getJSONObject(position).getString("idComp"));
                                        fecha = jsonArray.getJSONObject(position).getString("fecha");
                                        titulo = jsonArray.getJSONObject(position).getString("titulo");
                                        descrip = jsonArray.getJSONObject(position).getString("descrip");
                                        hora = jsonArray.getJSONObject(position).getString("hora");
                                        nombre = jsonArray.getJSONObject(position).getString("nombre");

                                        //Paso de todos los Datos a la siguiente Activity:
                                        Intent intent = new Intent(Lista_tareas.this, TareaCompartida.class);
                                        intent.putExtra("idUsuario2",idUsuario2);
                                        intent.putExtra("idComp",idComp);
                                        intent.putExtra("fecha",fecha);
                                        intent.putExtra("titulo",titulo);
                                        intent.putExtra("descrip",descrip);
                                        intent.putExtra("hora",hora);
                                        intent.putExtra("nombre",nombre);
                                        startActivity(intent);
                                    }catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }else if (booleano == 0 && respuesta.equals("0")){
                            textoFechas.setText("No tienes tareas compartidas con este amigo, crea alguna.");
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
                Toast.makeText(Lista_tareas.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Al PHP le pasamos el idUsuario y idUsuario2:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idUsuario", String.valueOf(idUsuario));
                parametros.put("idUsuario2", String.valueOf(idUsuario2));
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest2);
    }
    //Insertar nueva tarea con nuestro amigo pasandole el idUsuario2:
    public void InsertarTarea(View view){
        Intent intent = new Intent(Lista_tareas.this, Insertar_Tarea_Compartida.class);
        intent.putExtra("idUsuario2",idUsuario2);
        startActivity(intent);
    }
    //Volver atras:
    public void VlistaAmigos(View view){
        Intent intent = new Intent(Lista_tareas.this, ListaAmigosTareas.class);
        intent.putExtra("idUsuario2",idUsuario2);
        startActivity(intent);
    }

}
