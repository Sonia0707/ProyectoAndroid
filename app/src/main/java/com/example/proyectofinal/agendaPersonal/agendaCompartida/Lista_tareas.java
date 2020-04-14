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

    ImageView volverAmigos3;
    ListView idFechasTitulo;
    String fecha,titulo,descrip,hora,nombre;
    TextView textoFechas;
    ArrayList<String> listFechasTitulo= new ArrayList<>();
    int idUsuario,idUsuario2, idComp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tareas);
        volverAmigos3=(ImageView)findViewById(R.id.volverAmigos3);
        idFechasTitulo=(ListView)findViewById(R.id.idFechasTitulo);
        textoFechas=(TextView)findViewById(R.id.textoFechas);

        //Sharpreferens idUsuario:
        SharedPreferences preferences = getSharedPreferences("LoginUsuario", Context.MODE_PRIVATE);
        idUsuario = preferences.getInt("idUsuario", 0);

        idUsuario2= getIntent().getIntExtra("idUsuario2",0);

        UrlFechas("http://192.168.1.131/ProyectoNuevo/AgendaCompartida/fechasTitulo.php");

    }
    public void UrlFechas(String URL){

        //2º En la siguiente línea hacemos uso de un objeto tipo StringRequest y luego dentro del constructor de la
        //clase colocamos como parámetros el tipo de método de envío (POST) la URL y seguidamente
        //agregamos la clase response listener:

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            // 3º La cual nos generará automaticamente el listener onResponse que éste reaccionara en
            //caso de que la petición se procese:
            @Override
            public void onResponse(String response) {


                //Validamos que el response no esté vacío esto dará a entender que el usuario y password
                // ingresados existen y que el servicio php nos está devolviendo la fila encontrada
                if (!response.isEmpty()){

                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String respuesta= jsonObject.getString("fechas");
                        int booleano= jsonObject.getInt("booleano");

                        if (booleano == 1){
                            textoFechas.setText("Lista de Tareas:");
                            final JSONArray jsonArray = new JSONArray(respuesta);

                            int cont=0;
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                cont++;
                                JSONObject object = jsonArray.getJSONObject(i);
                                String finalstring = cont+"."+object.getString("titulo")+" -> "+object.getString("fecha");
                                listFechasTitulo.add(finalstring);
                            }

                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(Lista_tareas.this, android.R.layout.simple_spinner_item, listFechasTitulo);
                            idFechasTitulo.setAdapter(adapter);
                            idFechasTitulo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    try{
                                        idUsuario2 = Integer.parseInt(jsonArray.getJSONObject(position).getString("idUsuario"));
                                        idComp = Integer.parseInt(jsonArray.getJSONObject(position).getString("idComp"));
                                        fecha = jsonArray.getJSONObject(position).getString("fecha");
                                        titulo = jsonArray.getJSONObject(position).getString("titulo");
                                        descrip = jsonArray.getJSONObject(position).getString("descrip");
                                        hora = jsonArray.getJSONObject(position).getString("hora");
                                        nombre = jsonArray.getJSONObject(position).getString("nombre");

                                        Toast.makeText(Lista_tareas.this, "idComp: "+idComp, Toast.LENGTH_SHORT).show();

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
            //4º Agregaremos la clase Response.ErrorListener() este nos generará el listener de un error response
            //el cual reaccionará en caso de no procesarse la petición al servidor:
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Comprobacion de se la conexion es correcta entre Android y el Servidor:
                Toast.makeText(Lista_tareas.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }

        }){//5º Agregamos el método getParams() dentro de éste colocaremos los parámetros que nuestro servicio solicita
            //para devolvernos una respuesta:
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //En el primer parámetro se colocará el nombre de la variable tipo POST que declaramos en nuestro servicio PHP y en
                //el segundo agregaremos el dato que deseamos enviar, en este caso nuestros EditText:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idUsuario", String.valueOf(idUsuario));
                parametros.put("idUsuario2", String.valueOf(idUsuario2));


                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };

        //6º Por ulltimo hacemos uso de la clase RequestQueue creamos una instancia de ésta y en la siguiente línea agregaremos la
        //instancia de nuestro objeto stringRequest ésta nos ayudará a procesar todas las peticiones hechas:
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest2);
    }
    public void InsertarTarea(View view){
        Intent intent = new Intent(Lista_tareas.this, Insertar_Tarea_Compartida.class);
        intent.putExtra("idUsuario2",idUsuario2);
        startActivity(intent);

    }
    public void VerTareas(View view){
        Intent intent = new Intent(Lista_tareas.this, TareaCompartida.class);
        intent.putExtra("idComp",idComp);
        startActivity(intent);

    }

}
