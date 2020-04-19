package com.example.proyectofinal.agendaPersonal;

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

public class TareasPersonales extends AppCompatActivity {

    //Crear varibles para visualizar y las que necesitemos:
    ImageView SubVolver;
    ListView lista2;
    TextView noContenido2;
    String titulo,descrip,hora,fecha,idRoles;
    int idPersonal,idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas_personales);

        lista2= (ListView)findViewById(R.id.idTareasPersonales);
        noContenido2= (TextView) findViewById(R.id.noContenido2);
        SubVolver= (ImageView) findViewById(R.id.SubVolver);

        //Sharpreferens idUsuario:
        SharedPreferences preferences = getSharedPreferences("LoginUsuario", Context.MODE_PRIVATE);
        idUsuario = preferences.getInt("idUsuario",0);

        //Recogemos el rol del Login para comprobar si es usuario o administrador y dependiendo de eso se manda a una actiivity u otra:
        SharedPreferences preferences2 = getSharedPreferences("idRoles", Context.MODE_PRIVATE);
        idRoles=preferences2.getString("idRoles","");

        //De primeras que conete con la URL de nuestro servidor PHP
        UrlTareas("http://192.168.1.131/ProyectoNuevo/AgendaPersonal/fechasContenido.php");

    }
    public void UrlTareas(String URL){

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
                        String respuesta= jsonObject.getString("contenido");
                        int booleano= jsonObject.getInt("booleano");

                        if (booleano == 1){
                            //Si el booleano es 1 se muestra este testo un textview:
                            noContenido2.setText("Titulos y horas de tus subtareas:");
                            //Metemos los resultados en una Arraylist que luego introduciremos en la Listview con un Array adapter:
                            final ArrayList<String> listSubTareas = new ArrayList<>();
                            final JSONArray jsonArray = new JSONArray(respuesta);
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                //En esta activity solo mostraremos el titulo de la tarea y la fecha:
                                String finalstring = "Nombre: "+object.getString("titulo") + " -> Fecha: " + object.getString("fecha");
                                listSubTareas.add(finalstring);
                            }

                            final ArrayAdapter adapter = new ArrayAdapter<String>(TareasPersonales.this, android.R.layout.simple_list_item_1, listSubTareas);

                            lista2.setAdapter(adapter);

                            lista2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    try {
                                        //Al hacer clic en la tarea nos llevara a la siguienta activity pasandole los demás datos que hemos sacado en el JSON
                                        idPersonal = Integer.parseInt(jsonArray.getJSONObject(position).getString("idPersonal"));
                                        fecha = jsonArray.getJSONObject(position).getString("fecha");
                                        titulo = jsonArray.getJSONObject(position).getString("titulo");
                                        descrip = jsonArray.getJSONObject(position).getString("descrip");
                                        hora = jsonArray.getJSONObject(position).getString("hora");

                                        //Activity donde se verá la tarea al completo:
                                        Intent intent = new Intent(TareasPersonales.this,Mi_tarea.class);

                                        intent.putExtra("idPersonal",idPersonal);
                                        intent.putExtra("titulo",titulo);
                                        intent.putExtra("descrip",descrip);
                                        intent.putExtra("hora",hora);
                                        intent.putExtra("fecha",fecha);
                                        startActivity(intent);
                                    }
                                    catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                            //Si el booleano es 0 y la respuesta tambien, querra decir que no hay datos y mostraremos el siguiente mensaje:
                        }else if (booleano == 0 && respuesta.equals("0")){
                            noContenido2.setText("No tienes tareas, inserta alguna.");

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
                Toast.makeText(TareasPersonales.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }

        }){//4º Agregamos el método getParams() dentro de éste colocaremos los parámetros que nuestro servicio solicita
            //para devolvernos una respuesta:
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //En el primer parámetro se colocará el nombre de la variable tipo POST que declaramos en nuestro servicio PHP y en
                //el segundo agregaremos el dato que deseamos enviar, en este caso nuestros EditText:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idUsuario", String.valueOf(idUsuario));


                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };

        //5º Por ulltimo hacemos uso de la clase RequestQueue creamos una instancia de ésta y en la siguiente línea agregaremos la
        //instancia de nuestro objeto stringRequest ésta nos ayudará a procesar todas las peticiones hechas:
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest2);
    }

    //Hacemos un ONclic para para que nos lance a otra Activity y poder introducir los datos de la nueva tarea, pasadole el idUsuario:
    public void nuevaTarea(View view){

        Intent intent = new Intent(getApplicationContext(), Insertar_Tarea.class);
        intent.putExtra("idUsuario",idUsuario);
        startActivity(intent);
        finish();
    }

    //volever atras según el rol que tengamos:
    public void volverTareas(View view){

        if (idRoles.equals("1")){
            startActivity(new Intent(this, Administrador.class));
            finish();

        }else if (idRoles.equals("2")){
            startActivity(new Intent(this, Usuario.class));
            finish();
        }
    }
}
