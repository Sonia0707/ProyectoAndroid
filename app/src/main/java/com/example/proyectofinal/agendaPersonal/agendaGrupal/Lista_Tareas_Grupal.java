package com.example.proyectofinal.agendaPersonal.agendaGrupal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lista_Tareas_Grupal extends AppCompatActivity {

    RecyclerView recyclerView2;
    RecyclerView.Adapter adapter3;
    RecyclerView.LayoutManager manager;
    List<ClaseGrupal> tareasGrup= new ArrayList<>();
    TextView nofechas;
    String titulo,descrip,hora,fechaGrupo,nombre;
    int idGrupal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista__tareas__grupal);
        recyclerView2 = (RecyclerView) findViewById(R.id.idTareasGrupales);
        manager = new LinearLayoutManager(this);
        recyclerView2.setLayoutManager(manager);

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView2.addItemDecoration(divider);


        nofechas= (TextView) findViewById(R.id.nofechas);

        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        //Recoger idGrupal y Nombre para consultas y mostrarlo:
        idGrupal= getIntent().getIntExtra("idGrupal",0);


        //De primeras que conete con la URL de nuestro servidor PHP
        UrlTareasGrupales("http://192.168.1.131/ProyectoNuevo/AgendaGrupal/listaTareasGrupal.php");
    }
    //Metodo para mostrar menu oculto:
    public boolean onCreateOptionsMenu(Menu menu3){
        getMenuInflater().inflate(R.menu.menu3, menu3);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.item6){

            Intent intent = new Intent(Lista_Tareas_Grupal.this, VerUsuariosGrupal.class);
            intent.putExtra("idGrupal",idGrupal);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
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

                            final JSONArray jsonArray = new JSONArray(respuesta);
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                //En esta activity solo mostraremos el titulo de la tarea y la fecha:
                                idGrupal = object.getInt("idGrupal");
                                fechaGrupo = object.getString("fecha");
                                titulo = object.getString("titulo");
                                descrip = object.getString("descrip");
                                nombre = object.getString("nombre");
                                hora = object.getString("hora");
                                tareasGrup.add(new ClaseGrupal(R.drawable.ic_launcher_icon_tareas_foreground,idGrupal,titulo,hora,nombre,fechaGrupo,descrip));

                            }

                            adapter3= new AdaptadorGrupal(Lista_Tareas_Grupal.this,tareasGrup);
                            recyclerView2.setAdapter(adapter3);

                            moverItems3(recyclerView2,tareasGrup);

                            ((AdaptadorGrupal)adapter3).setOnItemClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Activity donde se verá la tarea al completo:
                                    Intent intent = new Intent(Lista_Tareas_Grupal.this, TareaGrupal.class);// Cambiar el intem
                                    intent.putExtra("idGrupal",tareasGrup.get(recyclerView2.getChildAdapterPosition(v)).getIdGrupal());
                                    intent.putExtra("nombre",tareasGrup.get(recyclerView2.getChildAdapterPosition(v)).getNombrePersona());
                                    intent.putExtra("titulo",tareasGrup.get(recyclerView2.getChildAdapterPosition(v)).getTitulo());
                                    intent.putExtra("fecha",tareasGrup.get(recyclerView2.getChildAdapterPosition(v)).getFechaTareas());
                                    intent.putExtra("descrip",tareasGrup.get(recyclerView2.getChildAdapterPosition(v)).getDescripcion());
                                    intent.putExtra("hora",tareasGrup.get(recyclerView2.getChildAdapterPosition(v)).getHora());
                                    startActivity(intent);

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
    public void moverItems3(RecyclerView recyclerView, final List list){
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {

                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();

                Collections.swap(list,position_dragged,position_target);

                adapter3.notifyItemMoved(position_dragged,position_target);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        helper.attachToRecyclerView(recyclerView);

    }
}
