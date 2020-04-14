package com.example.proyectofinal.agendaPersonal.agendaCompartida;

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
import com.example.proyectofinal.agendaPersonal.Login_Main;
import com.example.proyectofinal.agendaPersonal.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PeticionAmigos extends AppCompatActivity {

    ArrayList<String> listUsuarios = new ArrayList<>();
    ArrayList<String>AmigosPeticiones= new ArrayList<>();
    ListView listViewUsuarios;
    TextView noUsuarios;
    EditText buscarUsuarios;
    int idUsuario,idUsuario2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peticion_amigos);
        listViewUsuarios=(ListView)findViewById(R.id.peticiones);
        buscarUsuarios=(EditText) findViewById(R.id.editBuscar);
        noUsuarios=(TextView)findViewById(R.id.noUsuarios);

        //Sharpreferens idUsuario:
        SharedPreferences preferences = getSharedPreferences("LoginUsuario", Context.MODE_PRIVATE);
        idUsuario = preferences.getInt("idUsuario", 0);

        buscarUsuariosList("http://192.168.1.131/ProyectoNuevo/AgendaCompartida/todosUsuarios.php");


    }
    public void buscarUsuariosList(String URL){

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
                        String respuesta= jsonObject.getString("usuarios");
                        int booleano= jsonObject.getInt("booleano");

                        if (booleano == 1){
                            noUsuarios.setText("Lista de Usuarios:");
                            final JSONArray jsonArray = new JSONArray(respuesta);

                            for (int i = 0; i <jsonArray.length() ; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String finalstring = object.getString("nombre");

                                listUsuarios.add(finalstring);
                            }
                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(PeticionAmigos.this, android.R.layout.simple_spinner_item, listUsuarios);
                            listViewUsuarios.setAdapter(adapter);
                            listViewUsuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    try{
                                        idUsuario2 = Integer.parseInt(jsonArray.getJSONObject(position).getString("idUsuario"));
                                        Toast.makeText(PeticionAmigos.this, "Pulsado: "+idUsuario2, Toast.LENGTH_SHORT).show();
                                        peticionAmigos("http://192.168.1.131/ProyectoNuevo/AgendaCompartida/peticion.php?");
                                    }catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            buscarUsuarios.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    adapter.getFilter().filter(s);

                                }

                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });



                        }else if (booleano == 0 && respuesta.equals("0")){
                            noUsuarios.setText("No hay usuarios");

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
                Toast.makeText(PeticionAmigos.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
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
    public void peticionAmigos(String URL){

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
                        String respuesta= jsonObject.getString("respuesta");

                        //4ºHacemos una serie de restricciones en las peticiones:

                        //A) El usuario ya ha enviado la peticion por lo tanto no puede volver a mandarla:
                        if (respuesta.equals("2")){

                            Toast.makeText(PeticionAmigos.this, "Ya has echo petición de amistad", Toast.LENGTH_SHORT).show();

                            //B) Ya son amigo por lo tanto no le puede mandar ninguna petición:
                        }else if (respuesta.equals("1")){

                            Toast.makeText(PeticionAmigos.this, "Ya sois amigos", Toast.LENGTH_SHORT).show();

                            //C) Peticion de amistad mandada esperando respuesta:
                        }else if (respuesta.equals("0")){
                            Toast.makeText(PeticionAmigos.this, "Petición mandada esperando respuesta...", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ListaAmigosTareas.class);
                            startActivity(intent);
                            finish();

                            //D) Problemas con la base de datos:
                        }else if (respuesta.equals("3")){
                            Toast.makeText(PeticionAmigos.this, "Problema en la base de datos intentelo mas tarde", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ListaAmigosTareas.class);
                            startActivity(intent);
                            finish();
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
                Toast.makeText(PeticionAmigos.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }

        }){//5º Agregamos el método getParams() dentro de éste colocaremos los parámetros que nuestro servicio solicita
            //para devolvernos una respuesta:
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //En el primer parámetro se colocará el nombre de la variable tipo POST que declaramos en nuestro servicio PHP y en
                //el segundo agregaremos el dato que deseamos enviar, en este caso nuestros EditText:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idUsuario2", String.valueOf(idUsuario2));
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
    public void volverListAmigos2(View view){
        Intent intent = new Intent(getApplicationContext(), ListaAmigosTareas.class);
        startActivity(intent);
        finish();

    }

}

