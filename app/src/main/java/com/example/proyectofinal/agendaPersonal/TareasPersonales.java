package com.example.proyectofinal.agendaPersonal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TareasPersonales extends AppCompatActivity {

    //Crear varibles para visualizar y las que necesitemos:
    ImageView SubVolver;
    RecyclerView lista2;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager manager;
    TextView noContenido2;
    String titulo,descrip,hora,fecha,idRoles;
    int idPersonal,idUsuario;
    List<LisviewTareas> listatareas = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas_personales);

        lista2= (RecyclerView) findViewById(R.id.recycler);

        manager = new LinearLayoutManager(this);
        lista2.setLayoutManager(manager);

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        lista2.addItemDecoration(divider);

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
                            noContenido2.setText("Lista de tareas");
                            //Metemos los resultados en una Arraylist que luego introduciremos en la Listview con un Array adapter:

                            final JSONArray jsonArray = new JSONArray(respuesta);
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                //En esta activity solo mostraremos el titulo de la tarea y la fecha:

                                String nombre = object.getString("titulo");
                                String fech = object.getString("fecha");
                                String hora = object.getString("hora");
                                String descripcion = object.getString("descrip");
                                int idPersonal = object.getInt("idPersonal");

                                listatareas.add(new LisviewTareas(nombre,fech,R.drawable.ic_launcher_icon_tareas_foreground,idPersonal,hora,descripcion));
                            }
                            adapter = new Adaptador(listatareas,TareasPersonales.this);
                            lista2.setAdapter(adapter);

                            moverItems(lista2,listatareas);

                            ((Adaptador)adapter).setOnItemClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Activity donde se verá la tarea al completo:
                                    Intent intent = new Intent(TareasPersonales.this,Mi_tarea.class);

                                    intent.putExtra("idPersonal",listatareas.get(lista2.getChildAdapterPosition(v)).getIdPersonal());
                                    intent.putExtra("titulo",listatareas.get(lista2.getChildAdapterPosition(v)).getNombreTarea());
                                    intent.putExtra("descrip",listatareas.get(lista2.getChildAdapterPosition(v)).getDescripcion());
                                    intent.putExtra("hora",listatareas.get(lista2.getChildAdapterPosition(v)).getHora());
                                    intent.putExtra("fecha", listatareas.get(lista2.getChildAdapterPosition(v)).getFechaTareas());
                                    startActivity(intent);

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
    public void moverItems(RecyclerView recyclerView, final List list){


        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {

                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();

                Collections.swap(list,position_dragged,position_target);

                adapter.notifyItemMoved(position_dragged,position_target);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        helper.attachToRecyclerView(recyclerView);


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
