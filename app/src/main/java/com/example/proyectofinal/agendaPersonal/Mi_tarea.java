package com.example.proyectofinal.agendaPersonal;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyectofinal.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
public class Mi_tarea extends AppCompatActivity implements View.OnClickListener {
    //Crear varibles para visualizar y las que necesitemos:
    ImageView volver;
    EditText idHora,idTitulo,idDescrip,idFecha;
    String titulo,descrip,hora,fecha;
    int idPersonal;

    //String para los 0 y barras en las fechas:
    private static final String CERO = "0";
    private static final String BARRA = "/";

    //Calendario para obtener fecha & hora:
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha:
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    //String para los puntos del medio en las horas
    private static final String DOS_PUNTOS = ":";

    //Variables para obtener la hora:
    final int horas = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_tarea);
        idHora =(EditText) findViewById(R.id.idHora);
        idTitulo=(EditText) findViewById(R.id.idTitulo);

        idDescrip=(EditText) findViewById(R.id.descrip);
        volver=(ImageView)findViewById(R.id.volver);
        idFecha=(EditText)findViewById(R.id.idFecha);

        //Recuperamos el contenido sacado en la Activity de la Tarea personal y lo visualizamos en los EditText:
        idPersonal= getIntent().getIntExtra("idPersonal",0);
        titulo= getIntent().getStringExtra("titulo");
        descrip= getIntent().getStringExtra("descrip");
        hora= getIntent().getStringExtra("hora");
        fecha= getIntent().getStringExtra("fecha");

        idFecha.setOnClickListener(this);
        idHora.setOnClickListener(this);

        idHora.setText(hora);
        idTitulo.setText(titulo);
        idDescrip.setText(descrip);
        idFecha.setText(fecha);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.idFecha:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                        final int mesActual = month + 1;
                        //Formateo el día obtenido: antepone el 0 si son menores de 10
                        String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                        //Formateo el mes obtenido: antepone el 0 si son menores de 10
                        String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
                        //Muestro la fecha con el formato deseado
                        idFecha.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);

                    }
                },anio,mes,dia);
                datePickerDialog.show();

                break;
            case R.id.idHora:
                TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //Formateo el hora obtenido: antepone el 0 si son menores de 10
                        String horaFormateada =  (hourOfDay < 10)? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                        //Formateo el minuto obtenido: antepone el 0 si son menores de 10
                        String minutoFormateado = (minute < 10)? String.valueOf(CERO + minute):String.valueOf(minute);
                        //Obtengo el valor a.m. o p.m., dependiendo de la selección del usuario
                        String AM_PM;
                        if(hourOfDay < 12) {
                            AM_PM = "a.m.";
                        } else {
                            AM_PM = "p.m.";
                        }
                        //Muestro la hora con el formato deseado
                        idHora.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + " " + AM_PM);
                    }
                    //Estos valores deben ir en ese orden
                    //Al colocar en false se muestra en formato 12 horas y true en formato 24 horas
                    //Pero el sistema devuelve la hora en formato 24 horas
                }, horas,minuto,false);
                //Lo lanzamos:
                timePickerDialog.show();
                break;
        }

    }
    //Boton que conecta con la URL de PHP para modificar la tarea:
    public void modificar(View view){
        modificarSubtarea("http://192.168.1.131/ProyectoNuevo/AgendaPersonal/modificarTarea.php");
    }
    public void modificarSubtarea(String UrlModificar){
        //1º En la siguiente línea hacemos uso de un objeto tipo StringRequest y luego dentro del constructor de la
        //clase colocamos como parámetros el tipo de método de envío (POST) la URL y seguidamente
        //agregamos la clase response listener:

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlModificar, new Response.Listener<String>() {

            // 2º La cual nos generará automaticamente el listener onResponse que éste reaccionara en
            //caso de que la petición se procese:
            @Override
            public void onResponse(String response) {
                //Validamos que el response si no falla nos da a entender que la conexión es buena
                if (!response.isEmpty()){
                    //Recogemos la respuesta del PHP con el JSON:
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int respuesta= jsonObject.getInt("respuesta");

                        //A) Si la respuesta es igual a 1 lanzamos Toast que la tarea a sido modificada:
                        if (respuesta == 1){
                            Toast.makeText(Mi_tarea.this, "La Tarea ha sido modificada", Toast.LENGTH_SHORT).show();

                            //Lanzamos a la activity de TareasPersonales para ver si el contenido se ha modificado:
                            Intent intent = new Intent(getApplicationContext(),TareasPersonales.class);
                            intent.putExtra("idPersonal",idPersonal);
                            startActivity(intent);
                            finish();

                            //B) La conexión con la base de datos es erronia, lanzaremos el mensaje para que le usurio sepa que hay un problema.
                        }else if (respuesta == 0){

                            Toast.makeText(Mi_tarea.this, "Error de conexion con la base de datos, intentelo mas tarde", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(Mi_tarea.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }

        }){//5º Agregamos el método getParams() dentro de éste colocaremos los parámetros que nuestro servicio solicita
            //para devolvernos una respuesta:
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Colocaremos los parametros recibidos de la otra activity por el Inten para mandarselo al PHP:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idPersonal", String.valueOf(idPersonal));
                parametros.put("titulo", idTitulo.getText().toString());
                parametros.put("descrip", idDescrip.getText().toString());
                parametros.put("hora", idHora.getText().toString());
                parametros.put("fecha", idFecha.getText().toString());
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };

        //6º Por ulltimo hacemos uso de la clase RequestQueue creamos una instancia de ésta y en la siguiente línea agregaremos la
        //instancia de nuestro objeto stringRequest ésta nos ayudará a procesar todas las peticiones hechas:
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }
    //Boton que conecta con la URL de PHP para elimnar la tarea:
    public void eliminar(View view){
        eliminarSubTarea("http://192.168.1.131/ProyectoNuevo/AgendaPersonal/eliminarTarea.php");
    }

    public void eliminarSubTarea(String Url){
        //1º En la siguiente línea hacemos uso de un objeto tipo StringRequest y luego dentro del constructor de la
        //clase colocamos como parámetros el tipo de método de envío (POST) la URL y seguidamente
        //agregamos la clase response listener:

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {

            // 2º La cual nos generará automaticamente el listener onResponse que éste reaccionara en
            //caso de que la petición se procese:
            @Override
            public void onResponse(String response) {

                //Validamos que el response si no falla nos da a entender que la conexión es buena
                if (!response.isEmpty()){
                    try {
                        //Recogemos la respuesta del PHP con el JSON:
                        JSONObject jsonObject = new JSONObject(response);
                        int respuesta= jsonObject.getInt("respuesta");

                        //A) Si la respuesta es uno elimanamos la tarea: y lanzamos el Toast indicandolo
                        if (respuesta == 1){
                            Toast.makeText(Mi_tarea.this, "Tarea eliminada", Toast.LENGTH_SHORT).show();

                            //Lanzamos a la activity de TareaPersonal para ver si el contenido esta borrado:
                            Intent intent = new Intent(getApplicationContext(),TareasPersonales.class);
                            intent.putExtra("idPersonal",idPersonal);
                            startActivity(intent);
                            finish();

                            //B) La conexión con la base de datos es erronia, lanzaremos el mensaje para que le usurio sepa que hay un problema.
                        }else if (respuesta == 0){

                            Toast.makeText(Mi_tarea.this, "Error de conexion con la base de datos, intentelo mas tarde", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(Mi_tarea.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }

        }){//5º Agregamos el método getParams() dentro de éste colocaremos los parámetros que nuestro servicio solicita
            //para devolvernos una respuesta:
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Le pasamos el idPersonal unico para borrar esa tarea en concreto:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idPersonal", String.valueOf(idPersonal));
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };

        //6º Por ulltimo hacemos uso de la clase RequestQueue creamos una instancia de ésta y en la siguiente línea agregaremos la
        //instancia de nuestro objeto stringRequest ésta nos ayudará a procesar todas las peticiones hechas:
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
    //Podemos ir otra vez a ver todas las tareas personales dandole a este boton:
    public void volver(View view){
        Intent intent = new Intent(getApplicationContext(),TareasPersonales.class);
        intent.putExtra("idPersonal",idPersonal);

        startActivity(intent);
        finish();
    }
}
