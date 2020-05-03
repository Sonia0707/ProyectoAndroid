package com.example.proyectofinal.agendaPersonal.agendaGrupal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import android.widget.EditText;
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
import com.example.proyectofinal.agendaPersonal.Administrador;
import com.example.proyectofinal.agendaPersonal.Usuario;
import com.example.proyectofinal.agendaPersonal.agendaCompartida.Insertar_Tarea_Compartida;
import com.example.proyectofinal.agendaPersonal.agendaCompartida.ListaAmigosTareas;
import com.example.proyectofinal.agendaPersonal.agendaCompartida.Lista_tareas;
import com.example.proyectofinal.agendaPersonal.agendaCompartida.VerPetiones;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lista_Grupos extends AppCompatActivity {

    RecyclerView recycler;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager manager;
    TextView noGrupos;
    List<ClaseListaGrupos> listGrupos= new ArrayList<>();
    int idUsuario, idGrupal,idUsuarioAdmin;
    String nombreG, idRoles, fecha, nombre;

    /*Faltan los 2 botones...*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista__grupos);
        recycler=(RecyclerView) findViewById(R.id.idListaGrupos);
        manager = new LinearLayoutManager(this);
        recycler.setLayoutManager(manager);

        //RecyclerView.ItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
       // recycler.addItemDecoration(divider);


        noGrupos=(TextView)findViewById(R.id.noGrupos);

        //Sharpreferens idUsuario:
        SharedPreferences preferences = getSharedPreferences("LoginUsuario", Context.MODE_PRIVATE);
        idUsuario = preferences.getInt("idUsuario", 0);

        //Recogemos el rol del Login para comprobar si es usuario o administrador y dependiendo de eso se manda a una actiivity u otra:
        SharedPreferences preferences2 = getSharedPreferences("idRoles", Context.MODE_PRIVATE);
        idRoles=preferences2.getString("idRoles","");



        ListaGrupos("http://192.168.1.131/ProyectoNuevo/AgendaGrupal/listaGrupos.php");

    }
    public void ListaGrupos(String URL){
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){

                    try {
                        //Recibimos la lista de Grupos a los que pertenecemos y los administradores que teienen
                        JSONObject jsonObject = new JSONObject(response);
                        String respuesta= jsonObject.getString("listAdmin");
                        int booleano= jsonObject.getInt("booleano");

                        if (booleano == 1){
                            //Si hay datos se muestra el siguiente edittext:
                            noGrupos.setText("Lista de Grupos:");
                            final JSONArray jsonArray = new JSONArray(respuesta);

                            for (int i = 0; i <jsonArray.length() ; i++) {
                                //Sacamos el nombre  del Grupo, la fecha de creación y el nombre del Administtrador:
                                JSONObject object = jsonArray.getJSONObject(i);
                                idGrupal = object.getInt("idGrupal");
                                idUsuarioAdmin = object.getInt("idAdmind");
                                nombreG = object.getString("nombreG");
                                fecha = object.getString("fecha");
                                nombre = object.getString("nombreAdmin");
                                listGrupos.add(new ClaseListaGrupos(R.drawable.ic_group_add_black_24dp,idUsuarioAdmin,idGrupal,nombreG,nombre,fecha));
                            }
                            adapter = new AdaptadorGrupos(getApplicationContext(),listGrupos);

                            recycler.setAdapter(adapter);

                            ((AdaptadorGrupos)adapter).setOnItemClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    guardarAdmin(idUsuarioAdmin);

                                    Intent intent = new Intent(Lista_Grupos.this, Lista_Tareas_Grupal.class);
                                    intent.putExtra("idGrupal",listGrupos.get(recycler.getChildAdapterPosition(v)).getIdGrupalG());
                                    startActivity(intent);

                                }
                            });


                        }else if (booleano == 0 && respuesta.equals("0")){
                            noGrupos.setText("No tienes grupos ni perteneces a ninguno, crea alguno");
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
                Toast.makeText(Lista_Grupos.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }

        }){//5º Agregamos el método getParams() dentro de éste colocaremos los parámetros que nuestro servicio solicita
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

        //6º Por ulltimo hacemos uso de la clase RequestQueue creamos una instancia de ésta y en la siguiente línea agregaremos la
        //instancia de nuestro objeto stringRequest ésta nos ayudará a procesar todas las peticiones hechas:
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest2);
    }

    public void insertarAmigos(View view){
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nombre de Grupo:");
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_layaut, null);
        builder.setView(customLayout);
        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                EditText editText = customLayout.findViewById(R.id.editText);
                sendDialogDataToActivity(editText.getText().toString());
            }
        });
        builder.setNegativeButton ("Cancelar", null);
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    // do something with the data coming from the AlertDialog
    private void sendDialogDataToActivity(String data) {
        nombreG = data;
        insertarNombreG("http://192.168.1.131/ProyectoNuevo/AgendaGrupal/insertarNombreG.php");
    }
   //
    public void insertarNombreG( String Url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){
                    try {
                        //Respuesta del JSON de PHP:
                        JSONObject jsonObject = new JSONObject(response);
                        int respuesta= jsonObject.getInt("respuesta");
                        int idGrupal= jsonObject.getInt("idGrupal");
                        //Si la respuesta es uno quiere decir que la conexion ha ido bien y se ha insertado en la tabla:
                        if (respuesta == 1){
                            Toast.makeText(Lista_Grupos.this, "Insertado Nombre", Toast.LENGTH_SHORT).show();
                            //Lanzamos a la activity de Nuevo Grupo para buscar los amirgos que queremos añadir en el gupo:
                            Intent intent = new Intent(getApplicationContext(),NuevoGrupo.class);
                            intent.putExtra("idGrupal",idGrupal); //Paso el idGrupal para insertar usurarios a ese grupo.
                            intent.putExtra("nombreG",nombreG); //Paso el nombreG para visualizarlo en la otra activity.
                            startActivity(intent);
                            finish();
                            //B) La conexión con la base de datos es erronia, lanzaremos el mensaje para que le usurio sepa que hay un problema.
                        }else if (respuesta == 0){
                            Toast.makeText(Lista_Grupos.this, "Error de conexion con la base de datos, intentelo mas tarde", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Lista_Grupos.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Pasamos los parametros modificados al PHP para que este los modifique en la tabla:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idUsuario", String.valueOf(idUsuario));
                parametros.put("nombreG", String.valueOf(nombreG));
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    //Guardemos datos de admistrador de Grupos para futuras cosas:
    public   void guardarAdmin(int idUsuarioAdmin){
        SharedPreferences preferences = getSharedPreferences("AdministradorGrupo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("idUsuarioAdmind",idUsuarioAdmin);
        editor.commit();
    }
    public void atras(View view){
        if (idRoles.equals("1")){
            startActivity(new Intent(this, Administrador.class));
            finish();

        }else if (idRoles.equals("2")){
            startActivity(new Intent(this, Usuario.class));
            finish();
        }
    }
}
