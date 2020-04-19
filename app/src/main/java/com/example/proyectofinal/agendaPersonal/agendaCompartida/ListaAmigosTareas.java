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
import com.example.proyectofinal.agendaPersonal.Administrador;
import com.example.proyectofinal.agendaPersonal.Usuario;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListaAmigosTareas extends AppCompatActivity{
    //Crear varibles para visualizar y las que necesitemos:
    ImageView volverPrincipal;
    ListView idAmigos;
    TextView noAmigos;
    ArrayList<String>Amigos= new ArrayList<>();
    int idUsuario,idUsuario2;
    String idRoles;
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

        //Recogemos el rol del Login para comprobar si es usuario o administrador y dependiendo de eso se manda a una actiivity u otra:
        SharedPreferences preferences2 = getSharedPreferences("idRoles", Context.MODE_PRIVATE);
        idRoles=preferences2.getString("idRoles","");

        //De primeras que conecte con la URL de nuestro servidor PHP
        UrlAmigos("http://192.168.1.131/ProyectoNuevo/AgendaCompartida/amigos.php");

    }
    //Metodo para ver los amigos que tenemos:
    public void UrlAmigos(String URL){
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){
                    try {
                        ////Recogemos el JSON y vemos si enrealidad tenemos contenido o no con la respuesta de Booleano que nos llega:
                        JSONObject jsonObject = new JSONObject(response);
                        String respuesta= jsonObject.getString("amigos");
                        int booleano= jsonObject.getInt("booleano");
                        //Si el booleano es 1 se muestra este testo un textview:
                        if (booleano == 1){
                            noAmigos.setText("Amigos y tareas:");
                            final JSONArray jsonArray = new JSONArray(respuesta);
                            int cont=0;
                            //Metemos los resultados en una Arraylist que luego introduciremos en la Listview con un Array adapter:
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
                                        //Al pulsar en la lista guardamos el idUsurio que nos a enviado el JSON
                                        idUsuario2 = Integer.parseInt(jsonArray.getJSONObject(position).getString("idUsuario"));
                                        Toast.makeText(ListaAmigosTareas.this, "Pulsado: "+idUsuario2, Toast.LENGTH_SHORT).show();
                                        //Y se lo mandamos a Lista tareas para ver que tareas tenemos con ese usuario en concreto:
                                        Intent intent = new Intent(ListaAmigosTareas.this, Lista_tareas.class);
                                        intent.putExtra("idUsuario2",idUsuario2);
                                        startActivity(intent);
                                    }catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        //Si no tenemos amigos tenemos que ir hacerlos a la pantalla principal y esperar a que nos acepten para compartir con ellos tareas:
                        }else if (booleano == 0 && respuesta.equals("0")){
                            noAmigos.setText("No tienes amigos busca y haz petición. O mira si tienes peticiones para aceptar..");

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
                Toast.makeText(ListaAmigosTareas.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Mandamos idUsuario para sacar todos los usuarios amigo de este:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idUsuario", String.valueOf(idUsuario));
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest2);
    }
    //Volver atras:
    public void VolverPrincipal(View view){

        if (idRoles.equals("1")){
            Intent intent = new Intent(getApplicationContext(), Administrador.class);
            intent.putExtra("idUsuario",idUsuario);
            startActivity(intent);
            finish();

        }else if (idRoles.equals("2")){
            Intent intent = new Intent(getApplicationContext(), Usuario.class);
            intent.putExtra("idUsuario",idUsuario);
            startActivity(intent);
            finish();
        }
    }
}
