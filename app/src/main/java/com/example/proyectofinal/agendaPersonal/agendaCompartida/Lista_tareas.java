package com.example.proyectofinal.agendaPersonal.agendaCompartida;

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

public class Lista_tareas extends AppCompatActivity {
    //Crear varibles para visualizar y las que necesitemos:
    RecyclerView idFechasTitulo;
    RecyclerView.Adapter adapter2;
    RecyclerView.LayoutManager manager;
    String fecha,titulo,descrip,hora,nombre;
    TextView textoFechas;
    List<TareasComp> tareasComp= new ArrayList<>();
    int idUsuario,idUsuario2, idComp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tareas);
        idFechasTitulo=(RecyclerView) findViewById(R.id.idFechasTitulo);
        manager = new LinearLayoutManager(this);
        idFechasTitulo.setLayoutManager(manager);

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        idFechasTitulo.addItemDecoration(divider);


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

                            for (int i = 0; i <jsonArray.length() ; i++) {

                                JSONObject object = jsonArray.getJSONObject(i);
                                //Sacamos todo el contenido pero para esta vista solo mostrasmos el titulo de la tarea y la fecha:

                                idUsuario2 = object.getInt("idUsuario");
                                idComp = object.getInt("idComp");
                                fecha = object.getString("fecha");
                                titulo = object.getString("titulo");
                                descrip = object.getString("descrip");
                                nombre = object.getString("nombre");
                                hora = object.getString("hora");
                                tareasComp.add(new TareasComp(R.drawable.ic_launcher_icon_tareas_foreground, idComp, titulo, hora, fecha, descrip, nombre,idUsuario2));
                            }
                            adapter2 = new AdaptadorComp(Lista_tareas.this,tareasComp);
                            idFechasTitulo.setAdapter(adapter2);

                           moverItems2(idFechasTitulo,tareasComp);

                            ((AdaptadorComp)adapter2).setOnItemClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Lista_tareas.this, TareaCompartida.class);
                                    intent.putExtra("idComp",tareasComp.get(idFechasTitulo.getChildAdapterPosition(v)).getIdComp());
                                    intent.putExtra("idUsuario2",tareasComp.get(idFechasTitulo.getChildAdapterPosition(v)).getIdUsuario2());
                                    intent.putExtra("titulo",tareasComp.get(idFechasTitulo.getChildAdapterPosition(v)).getTitulo());
                                    intent.putExtra("descrip",tareasComp.get(idFechasTitulo.getChildAdapterPosition(v)).getDescrip());
                                    intent.putExtra("hora",tareasComp.get(idFechasTitulo.getChildAdapterPosition(v)).getHoraComp());
                                    intent.putExtra("fecha", tareasComp.get(idFechasTitulo.getChildAdapterPosition(v)).getFechTareas());
                                    intent.putExtra("nombre", tareasComp.get(idFechasTitulo.getChildAdapterPosition(v)).getNombPersona());
                                    startActivity(intent);

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
    public void moverItems2 (RecyclerView recyclerView, final List list){
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {

                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();

                Collections.swap(list,position_dragged,position_target);

                adapter2.notifyItemMoved(position_dragged,position_target);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        helper.attachToRecyclerView(recyclerView);

    }

}
