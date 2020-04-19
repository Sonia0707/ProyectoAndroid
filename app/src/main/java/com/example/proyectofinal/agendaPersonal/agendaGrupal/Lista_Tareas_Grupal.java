package com.example.proyectofinal.agendaPersonal.agendaGrupal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.proyectofinal.agendaPersonal.Mi_tarea;
import com.example.proyectofinal.agendaPersonal.TareasPersonales;
import com.example.proyectofinal.agendaPersonal.agendaCompartida.Lista_tareas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lista_Tareas_Grupal extends AppCompatActivity {

    ListView listViewFechasGrupales;
    TextView nofechas;
    String titulo,descrip,hora,fechaGrupo,nombre;
    int idGrupal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista__tareas__grupal);
        listViewFechasGrupales= (ListView)findViewById(R.id.idTareasGrupales);
        nofechas= (TextView) findViewById(R.id.nofechas);

        //Recoger idGrupal y Nombre para consultas y mostrarlo:
        idGrupal= getIntent().getIntExtra("idGrupal",0);

        //De primeras que conete con la URL de nuestro servidor PHP
        UrlTareasGrupales("http://192.168.1.131/ProyectoNuevo/AgendaGrupal/listaTareasGrupal.php");
    }
    public void UrlTareasGrupales(String URL){

        //1º En la siguiente línea hacemos uso de un objeto tipo StringRequest y luego dentro del constructor de la
        //clase colocamos como parámetros el tipo de método de envío (POST) la URL y seguidamente
        //agregamos la clase response listener:

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            // 2º La cual nos generará automaticamente el listener onResponse que éste reaccionara en
            //caso de que la petición se procese:
            @Override
            public void onResponse(String response) {

                //Validamos que el response si no falla nos da a entender que la conexión es buena
                if (!response.isEmpty()){

                    try {
                        //Recogemos el JSON y vemos si enrealidad tenemos contenido o no con la respuesta de Booleano que nos llega:
                        JSONObject jsonObject = new JSONObject(response);
                        String respuesta= jsonObject.getString("respuesta");
                        int booleano= jsonObject.getInt("booleano");

                        if (booleano == 1){
                            //Si el booleano es 1 se muestra este testo un textview:
                            nofechas.setText("Titulos y fecha de las tareas:");
                            //Metemos los resultados en una Arraylist que luego introduciremos en la Listview con un Array adapter:
                            final ArrayList<String> listSubTareas = new ArrayList<>();
                            final JSONArray jsonArray = new JSONArray(respuesta);
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                //En esta activity solo mostraremos el titulo de la tarea y la fecha:
                                String finalstring = "Nombre: "+object.getString("titulo") + " -> Fecha: " + object.getString("fecha");
                                listSubTareas.add(finalstring);
                            }

                            final ArrayAdapter adapter = new ArrayAdapter<String>(Lista_Tareas_Grupal.this, android.R.layout.simple_list_item_1, listSubTareas);

                            listViewFechasGrupales.setAdapter(adapter);

                            listViewFechasGrupales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    try {
                                        //Al hacer clic en la tarea nos llevara a la siguienta activity pasandole los demás datos que hemos sacado en el JSON
                                        idGrupal = Integer.parseInt(jsonArray.getJSONObject(position).getString("idGrupal"));
                                        titulo = jsonArray.getJSONObject(position).getString("titulo");
                                        nombre = jsonArray.getJSONObject(position).getString("nombre");
                                        fechaGrupo = jsonArray.getJSONObject(position).getString("fecha");
                                        hora = jsonArray.getJSONObject(position).getString("hora");
                                        descrip = jsonArray.getJSONObject(position).getString("descrip");


                                        //Activity donde se verá la tarea al completo:
                                        Intent intent = new Intent(Lista_Tareas_Grupal.this, TareaGrupal.class);// Cambiar el intem

                                        intent.putExtra("idGrupal",idGrupal);
                                        intent.putExtra("nombre",nombre);
                                        intent.putExtra("titulo",titulo);
                                        intent.putExtra("fecha",fechaGrupo);
                                        intent.putExtra("descrip",descrip);
                                        intent.putExtra("hora",hora);
                                        startActivity(intent);
                                    }
                                    catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                            //Si el booleano es 0 y la respuesta tambien, querra decir que no hay datos y mostraremos el siguiente mensaje:
                        }else if (booleano == 0 && respuesta.equals("0")){
                            nofechas.setText("No tienes tareas en este grupo inserta alguna.");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
            //3º Agregaremos la clase Response.ErrorListener() este nos generará el listener de un error response
            //el cual reaccionará en caso de no procesarse la petición al servidor:
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Comprobacion de se la conexion es correcta entre Android y el Servidor:
                Toast.makeText(Lista_Tareas_Grupal.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }

        }){//4º Agregamos el método getParams() dentro de éste colocaremos los parámetros que nuestro servicio solicita
            //para devolvernos una respuesta:
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //En el primer parámetro se colocará el nombre de la variable tipo POST que declaramos en nuestro servicio PHP y en
                //el segundo agregaremos el dato que deseamos enviar, en este caso nuestros EditText:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idGrupal", String.valueOf(idGrupal));


                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };

        //5º Por ulltimo hacemos uso de la clase RequestQueue creamos una instancia de ésta y en la siguiente línea agregaremos la
        //instancia de nuestro objeto stringRequest ésta nos ayudará a procesar todas las peticiones hechas:
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest2);
    }
    public void volverG(View view){
        Intent intent = new Intent(Lista_Tareas_Grupal.this, Lista_Grupos.class);
        intent.putExtra("idGrupal",idGrupal);
        startActivity(intent);
    }
    public void insertarTareaGrupal(View view){
        Intent intent = new Intent(Lista_Tareas_Grupal.this, InsertarTareaGrupal.class);
        intent.putExtra("idGrupal",idGrupal);
        startActivity(intent);

    }
    public void verUsuarioas(View view){
        Intent intent = new Intent(Lista_Tareas_Grupal.this, VerUsuariosGrupal.class);//Crear Ver usuarios
        intent.putExtra("idGrupal",idGrupal);
        startActivity(intent);

    }
}
