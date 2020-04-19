package com.example.proyectofinal.agendaPersonal.agendaCompartida;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.proyectofinal.agendaPersonal.Administrador;
import com.example.proyectofinal.agendaPersonal.Configuracion;
import com.example.proyectofinal.agendaPersonal.TareasPersonales;
import com.example.proyectofinal.agendaPersonal.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class VerPetiones extends AppCompatActivity {
    //Crear varibles para visualizar y las que necesitemos:
    ArrayList<String> listpeticiones= new ArrayList<>();
    ListView listViewPeticiones;
    TextView textoPeticiones;
    int idUsuario,idUsuario2;
    String peticion,idRoles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_petiones);
        listViewPeticiones=(ListView)findViewById(R.id.idPeticiones);
        textoPeticiones=(TextView)findViewById(R.id.textoPeticiones2);

        //Sharpreferens idUsuario:
        SharedPreferences preferences = getSharedPreferences("LoginUsuario", Context.MODE_PRIVATE);
        idUsuario = preferences.getInt("idUsuario", 0);

        //Recogemos el rol del Login para comprobar si es usuario o administrador y dependiendo de eso se manda a una actiivity u otra:
        SharedPreferences preferences2 = getSharedPreferences("idRoles", Context.MODE_PRIVATE);
        idRoles=preferences2.getString("idRoles","");

        //De primeras que conecte con la URL de nuestro servidor PHP
        listPeticiones("http://192.168.1.131/ProyectoNuevo/AgendaCompartida/espera.php?");
    }
    //Metodo para ver las peticiones que nos han hecho:
    public void listPeticiones(String URL){
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Validamos que el response si no falla nos da a entender que la conexión es buena
                if (!response.isEmpty()){
                    try{
                        ////Recogemos el JSON y vemos si enrealidad tenemos contenido o no con la respuesta de Booleano que nos llega:
                        JSONObject jsonObject = new JSONObject(response);
                        String respuesta= jsonObject.getString("amigos");
                        int booleano= jsonObject.getInt("booleano");

                        if (booleano == 1){
                            //Si el booleano es 1 se muestra este testo un textview:
                            textoPeticiones.setText("Pulsa la peticion para aceptarla o rechazarla:");
                            final JSONArray jsonArray = new JSONArray(respuesta);
                            //Metemos los resultados en una Arraylist que luego introduciremos en la Listview con un Array adapter:
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                //Recogemos loe nombres:
                                String finalstring = object.getString("nombre");
                                listpeticiones.add(finalstring);
                            }
                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(VerPetiones.this, android.R.layout.simple_spinner_item, listpeticiones);
                            listViewPeticiones.setAdapter(adapter);
                            listViewPeticiones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //Con la pulsacion en la lista => Creamos un AlertDialogo para decidir si aceptamos la respuesta o no:
                                    dialogoPeticiones(jsonArray,position);
                                }
                            });
                        //Si no hubiera ningún peticion:
                        }else if (booleano == 0 && respuesta.equals("0")){
                            textoPeticiones.setText("No hay peticiiones");
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
                Toast.makeText(VerPetiones.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Pasamos el idUsuario:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idUsuario", String.valueOf(idUsuario));
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest2);
    }
    //El metodo dialogoPeticiones que contiene el JSON y la poscion de la lista:
    public void dialogoPeticiones(final JSONArray jsonArray, final int posicion){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Dos opciones:
        // Si => Conecta con el PHP de respuesta para cambiar en la mysql la respuesta a 1:
        builder.setMessage("¿ Deseas aceptar la peticion ?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            idUsuario2 = Integer.parseInt(jsonArray.getJSONObject(posicion).getString("idUsuario"));
                            Toast.makeText(VerPetiones.this, "Pulsado: "+idUsuario2, Toast.LENGTH_SHORT).show();
                            ///Acepta peticion:
                            peticion ="1";
                            respuestaPeticion("http://192.168.1.131/ProyectoNuevo/AgendaCompartida/respuesta.php?");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
                //No => Conecta con el PHP de respuesta para cambiar en la mysql la respuesta a 0:
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            idUsuario2 = Integer.parseInt(jsonArray.getJSONObject(posicion).getString("idUsuario"));
                            Toast.makeText(VerPetiones.this, "Pulsado: "+idUsuario2, Toast.LENGTH_SHORT).show();
                            ///Rechaza peticion:
                            peticion="0";
                            respuestaPeticion("http://192.168.1.131/ProyectoNuevo/AgendaCompartida/respuesta.php?");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        //Se lanza el dialogo
        AlertDialog alert = builder.create();
        alert.show();
    }
    //Metodo de respuesta dependiendo que peticion mandemos:
    public void respuestaPeticion(String URL){
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String respuesta= jsonObject.getString("respuesta");
                        //Vemos la respuesta JSON:

                        //A) El usuario acepta la petición de amistad:
                        if (respuesta.equals("1")){

                            Toast.makeText(VerPetiones.this, "Has aceptado la petición de amistad", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ListaAmigosTareas.class);
                            startActivity(intent);
                            finish();

                            //B) El usuario rechaza la solicitud:
                        }else if (respuesta.equals("0")){

                            Toast.makeText(VerPetiones.this, "Has rechazado la peticion de amistad", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ListaAmigosTareas.class);
                            startActivity(intent);
                            finish();

                            //C) Error en la base de datos:
                        }else if (respuesta.equals("ERROR")){
                            Toast.makeText(VerPetiones.this, "Problema en la base de datos intentelo mas tarde", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ListaAmigosTareas.class);
                            startActivity(intent);
                            finish();
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
                Toast.makeText(VerPetiones.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Mandamos los dos usuarios y la paticion aceptada o rechazada depende de lo que contestemos en el AlertDialogo:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idUsuario", String.valueOf(idUsuario));
                parametros.put("idUsuario2", String.valueOf(idUsuario2));
                parametros.put("peticion", String.valueOf(peticion));
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest2);
    }
    //Volver atras:
    public void atrasVerPerticiones(View view){

        if (idRoles.equals("1")){
            startActivity(new Intent(this, Administrador.class));
            finish();
        }else if (idRoles.equals("2")){
            startActivity(new Intent(this, Usuario.class));
            finish();
        }
    }
}
