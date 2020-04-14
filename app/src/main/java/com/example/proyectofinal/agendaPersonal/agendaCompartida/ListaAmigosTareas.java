package com.example.proyectofinal.agendaPersonal.agendaCompartida;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.example.proyectofinal.agendaPersonal.Mi_tarea;
import com.example.proyectofinal.agendaPersonal.TareasPersonales;
import com.example.proyectofinal.agendaPersonal.Usuario;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListaAmigosTareas extends AppCompatActivity{

    ImageView volverPrincipal;
    ListView idAmigos;

    TextView noAmigos;
    ArrayList<String>Amigos= new ArrayList<>();
    int idUsuario,idUsuario2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiry_lista_amigos_tarea);
        idAmigos=(ListView)findViewById(R.id.idAmigos);
        noAmigos=(TextView)findViewById(R.id.noAmigos);
        volverPrincipal=(ImageView)findViewById(R.id.volverPrincipal);


        //Sharpreferens idUsuario:
        SharedPreferences preferences = getSharedPreferences("LoginUsuario", Context.MODE_PRIVATE);
        idUsuario = preferences.getInt("idUsuario", 0);

        UrlAmigos("http://192.168.1.131/ProyectoNuevo/AgendaCompartida/amigos.php");

    }



    public void UrlAmigos(String URL){

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
                        String respuesta= jsonObject.getString("amigos");
                        int booleano= jsonObject.getInt("booleano");

                        if (booleano == 1){
                            noAmigos.setText("Amigos y tareas:");
                            final JSONArray jsonArray = new JSONArray(respuesta);

                            int cont=0;
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                cont++;
                                JSONObject object = jsonArray.getJSONObject(i);
                                String finalstring = cont+"."+object.getString("nombre");
                                Amigos.add(finalstring);
                            }
                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListaAmigosTareas.this, android.R.layout.simple_spinner_item, Amigos);
                            idAmigos.setAdapter(adapter);
                            idAmigos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    try{
                                        idUsuario2 = Integer.parseInt(jsonArray.getJSONObject(position).getString("idUsuario"));
                                        Toast.makeText(ListaAmigosTareas.this, "Pulsado: "+idUsuario2, Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(ListaAmigosTareas.this, Lista_tareas.class);
                                        intent.putExtra("idUsuario2",idUsuario2);
                                        startActivity(intent);

                                    }catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });


                        }else if (booleano == 0 && respuesta.equals("0")){
                            noAmigos.setText("No tienes amigos busca y haz petición. O mira si tienes peticiones para aceptar..");

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
                Toast.makeText(ListaAmigosTareas.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
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
    public void buscarAmigos(View view){
        Intent intent = new Intent(getApplicationContext(), PeticionAmigos.class);
        startActivity(intent);
        finish();

    }
    public void buscarPeticiones(View view){
        Intent intent = new Intent(getApplicationContext(), VerPetiones.class);
        startActivity(intent);
        finish();

    }
    public void VolverPrincipal(View view){
        Intent intent = new Intent(getApplicationContext(), Usuario.class);
        startActivity(intent);
        finish();

    }


}
