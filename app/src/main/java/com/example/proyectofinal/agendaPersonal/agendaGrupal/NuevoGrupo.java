package com.example.proyectofinal.agendaPersonal.agendaGrupal;

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
import android.widget.Button;
import android.widget.CheckedTextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NuevoGrupo extends AppCompatActivity {
    //Varibles necesarias y objetos para la vista:
    ArrayList<String> listaGrupo = new ArrayList<>();
    ListView listViewGrupo;
    TextView nombreG, noAmigosGrupo;
    int idGrupal,idUsuario,idUsuario2;
    String nombreGrupal,nombreUsu;
    Button btnGuardarGrupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_grupo);
        listViewGrupo= (ListView)findViewById(R.id.idAmigosGrupo);
        nombreG = (TextView)findViewById(R.id.idNombreG);
        noAmigosGrupo = (TextView)findViewById(R.id.noAmigosGrupo);
        btnGuardarGrupo= (Button)findViewById(R.id.btnGuardarGrupo);

        btnGuardarGrupo.setEnabled(false);

        //Recoger idGrupal y Nombre para consultas y mostrarlo:
        idGrupal= getIntent().getIntExtra("idGrupal",0);
        nombreGrupal= getIntent().getStringExtra("nombreG");

        //Sharpreferens idUsuario:
        SharedPreferences preferences = getSharedPreferences("LoginUsuario", Context.MODE_PRIVATE);
        idUsuario = preferences.getInt("idUsuario", 0);

        //De primeras que conecte con la URL de nuestro servidor PHP todos los amigos:
        amigos("http://192.168.1.131/ProyectoNuevo/AgendaCompartida/amigos.php");

    }
    public void amigos(String URL){
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){
                    try {
                        ////Recogemos el JSON y vemos si enrealidad tenemos contenido o no con la respuesta de Booleano que nos llega:
                        JSONObject jsonObject = new JSONObject(response);
                        String respuesta= jsonObject.getString("amigos");
                        int booleano= jsonObject.getInt("booleano");

                        if (booleano == 1){
                            //Si el booleano es 1 se muestra este testo un textview:
                            noAmigosGrupo.setText("Escoge a tus amigos y guarda el grupo:");
                            final JSONArray jsonArray = new JSONArray(respuesta);
                            //Metemos los resultados en una Arraylist que luego introduciremos en la Listview con un Array adapter:
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                //Recogemos loe nombres de los amigos y los mostramos en la lista:
                                String nombre = object.getString("nombre");
                                listaGrupo.add(nombre);
                            }
                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(NuevoGrupo.this, android.R.layout.simple_list_item_multiple_choice, listaGrupo);
                            listViewGrupo.setAdapter(adapter);
                            listViewGrupo.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                            listViewGrupo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                    if(listViewGrupo.isItemChecked(position)){
                                        try {
                                            btnGuardarGrupo.setEnabled(true);
                                            idUsuario2 = Integer.parseInt(jsonArray.getJSONObject(position).getString("idUsuario"));
                                            nombreUsu = jsonArray.getJSONObject(position).getString("nombre");
                                            Toast.makeText(NuevoGrupo.this, "Pulsado: "+idUsuario2, Toast.LENGTH_SHORT).show();
                                            guardarAmigosGrupo("http://192.168.1.131/ProyectoNuevo/AgendaGrupal/insertarAmigosGrupo.php");

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }else if (!listViewGrupo.isItemChecked(position)){


                                        try {
                                            idUsuario2 = Integer.parseInt(jsonArray.getJSONObject(position).getString("idUsuario"));
                                            nombreUsu = jsonArray.getJSONObject(position).getString("nombre");
                                            Toast.makeText(NuevoGrupo.this, "Pulsado: "+idUsuario2, Toast.LENGTH_SHORT).show();
                                            desclicarChect("http://192.168.1.131/ProyectoNuevo/AgendaGrupal/eliminarGrupo1.php");

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                }
                            });
                            //Si no hay amigos se mostrara el siguiente mensaje:
                        }else if (booleano == 0 && respuesta.equals("0")){
                            noAmigosGrupo.setText("No tienes amigos para crear grupo, cancela y haz peticiones o mira las peticiones:");
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
                Toast.makeText(NuevoGrupo.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
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
    public void guardar(View view){
        //Intem atras...
        Toast.makeText(NuevoGrupo.this, "Has creado el grupo", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(),Lista_Tareas_Grupal.class); // cambiaar a Tareas
        intent.putExtra("idGrupal",idGrupal); //Paso el idGrupal para insertar usurarios a ese grupo.
        startActivity(intent);
        finish();

    }
    public void cancelar(View view){
        //Intem
        //Eliminacion de todo el grupo:
        canceladoGrupo("http://192.168.1.131/ProyectoNuevo/AgendaGrupal/cancelarGrupo.php");
        Intent intent = new Intent(getApplicationContext(),Lista_Grupos.class); // cambiaar a Tareas
        startActivity(intent);
        finish();

    }
    public void  guardarAmigosGrupo(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){
                    try {
                        //Respuesta del JSON de PHP:
                        JSONObject jsonObject = new JSONObject(response);
                        int respuesta= jsonObject.getInt("respuesta");
                        //int idGrupal= jsonObject.getInt("idGrupal");
                        //Si la respuesta es uno quiere decir que la conexion ha ido bien y se ha insertado en la tabla:
                        if (respuesta == 1){

                            Toast.makeText(NuevoGrupo.this, "Has introducido: "+nombreUsu, Toast.LENGTH_SHORT).show();
                            //Lanzamos a la activity de Nuevo Grupo para buscar los amirgos que queremos añadir en el gupo:


                            //B) La conexión con la base de datos es erronia, lanzaremos el mensaje para que le usurio sepa que hay un problema.
                        }else if (respuesta == 0){
                            Toast.makeText(NuevoGrupo.this, "Error de conexion con la base de datos, intentelo mas tarde", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NuevoGrupo.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Pasamos los parametros modificados al PHP para que este los modifique en la tabla:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idUsuario2", String.valueOf(idUsuario2));
                parametros.put("idGrupal", String.valueOf(idGrupal));
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
    public void desclicarChect(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){
                    try {
                        //Respuesta del JSON de PHP:
                        JSONObject jsonObject = new JSONObject(response);
                        int respuesta= jsonObject.getInt("respuesta");
                        //int idGrupal= jsonObject.getInt("idGrupal");
                        //Si la respuesta es uno quiere decir que la conexion ha ido bien y se ha eliminado en la tabla:
                        if (respuesta == 1){
                            Toast.makeText(NuevoGrupo.this, "Has eliminado del grupo: "+nombreUsu, Toast.LENGTH_SHORT).show();

                            //B) La conexión con la base de datos es erronia, lanzaremos el mensaje para que le usurio sepa que hay un problema.
                        }else if (respuesta == 0){
                            Toast.makeText(NuevoGrupo.this, "Error de conexion con la base de datos, intentelo mas tarde", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NuevoGrupo.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Pasamos los parametros modificados al PHP para que este los modifique en la tabla:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idUsuario2", String.valueOf(idUsuario2));
                parametros.put("idGrupal", String.valueOf(idGrupal));
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public void canceladoGrupo(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){
                    try {
                        //Respuesta del JSON de PHP:
                        JSONObject jsonObject = new JSONObject(response);
                        int respuesta= jsonObject.getInt("respuesta");
                        //int idGrupal= jsonObject.getInt("idGrupal");
                        //Si la respuesta es uno quiere decir que la conexion ha ido bien y se ha eliminado en la tabla:
                        if (respuesta == 1){
                            Toast.makeText(NuevoGrupo.this, "Has cancelado el grupo", Toast.LENGTH_SHORT).show();

                            //B) La conexión con la base de datos es erronia, lanzaremos el mensaje para que le usurio sepa que hay un problema.
                        }else if (respuesta == 0){
                            Toast.makeText(NuevoGrupo.this, "Error de conexion con la base de datos, intentelo mas tarde", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NuevoGrupo.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Pasamos los parametros modificados al PHP para que este los modifique en la tabla:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idGrupal", String.valueOf(idGrupal));
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
