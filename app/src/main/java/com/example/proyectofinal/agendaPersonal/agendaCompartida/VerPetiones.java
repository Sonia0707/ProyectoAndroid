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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VerPetiones extends AppCompatActivity {

    ArrayList<String> listpeticiones= new ArrayList<>();
    ListView listViewPeticiones;
    TextView textoPeticiones;
    int idUsuario,idUsuario2;
    String peticion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_petiones);
        listViewPeticiones=(ListView)findViewById(R.id.idPeticiones);
        textoPeticiones=(TextView)findViewById(R.id.textoPeticiones2);

        //Sharpreferens idUsuario:
        SharedPreferences preferences = getSharedPreferences("LoginUsuario", Context.MODE_PRIVATE);
        idUsuario = preferences.getInt("idUsuario", 0);

        listPeticiones("http://192.168.1.131/ProyectoNuevo/AgendaCompartida/espera.php?");
    }

    public void listPeticiones(String URL){

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
                            textoPeticiones.setText("Manten pulsada la peticion para aceptarla o rechazarla:");
                            final JSONArray jsonArray = new JSONArray(respuesta);

                            for (int i = 0; i <jsonArray.length() ; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String finalstring = object.getString("nombre");

                                listpeticiones.add(finalstring);
                            }
                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(VerPetiones.this, android.R.layout.simple_spinner_item, listpeticiones);
                            listViewPeticiones.setAdapter(adapter);
                            listViewPeticiones.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    final int posicion=i;

                                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(VerPetiones.this);
                                    dialogo1.setTitle("Importante");
                                    dialogo1.setMessage("¿ Deseas aceptar la peticion ?");
                                    dialogo1.setCancelable(false);
                                    dialogo1.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo1, int id) {

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
                                    });
                                    dialogo1.setPositiveButton("Rechazar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo1, int id) {

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

                                    dialogo1.show();

                                    return false;
                                }
                            });

                        }else if (booleano == 0 && respuesta.equals("0")){
                            textoPeticiones.setText("No hay peticiiones");

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
                Toast.makeText(VerPetiones.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
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
    public void respuestaPeticion(String URL){

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

                        //A) El usuario acepta la petición de amistad:
                        if (respuesta.equals("1")){

                            Toast.makeText(VerPetiones.this, "Has aceptado la petición de amistad", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ListaAmigosTareas.class);
                            startActivity(intent);
                            finish();

                            //B) Ya son amigo por lo tanto no le puede mandar ninguna petición:
                        }else if (respuesta.equals("0")){

                            Toast.makeText(VerPetiones.this, "Has rechazado la peticion de amistad", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ListaAmigosTareas.class);
                            startActivity(intent);
                            finish();

                            //C) Peticion de amistad mandada esperando respuesta:
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
            //4º Agregaremos la clase Response.ErrorListener() este nos generará el listener de un error response
            //el cual reaccionará en caso de no procesarse la petición al servidor:
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Comprobacion de se la conexion es correcta entre Android y el Servidor:
                Toast.makeText(VerPetiones.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
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
                parametros.put("peticion", String.valueOf(peticion));



                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };

        //6º Por ulltimo hacemos uso de la clase RequestQueue creamos una instancia de ésta y en la siguiente línea agregaremos la
        //instancia de nuestro objeto stringRequest ésta nos ayudará a procesar todas las peticiones hechas:
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest2);
    }
}
