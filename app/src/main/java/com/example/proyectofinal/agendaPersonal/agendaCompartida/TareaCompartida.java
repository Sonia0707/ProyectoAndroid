package com.example.proyectofinal.agendaPersonal.agendaCompartida;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.proyectofinal.agendaPersonal.Mi_tarea;
import com.example.proyectofinal.agendaPersonal.TareasPersonales;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
public class TareaCompartida extends AppCompatActivity implements View.OnClickListener {
    //Crear varibles para visualizar y las que necesitemos:
    ImageView volver;
    String titulo,descrip,hora,fecha,nombre;
    EditText idHora,idTitulo,idDescrip,idFecha,idCompartida;
    int idComp,idUsuario2;
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
        setContentView(R.layout.activity_tarea_compartida);
        idHora =(EditText) findViewById(R.id.idHoraTareaComp);
        idTitulo=(EditText) findViewById(R.id.idTituloTareaComp);

        idDescrip=(EditText) findViewById(R.id.descripTareaComp);
        volver=(ImageView)findViewById(R.id.volverComp);
        idFecha=(EditText)findViewById(R.id.idFechaTaeaComp);
        idCompartida=(EditText)findViewById(R.id.idPersonaComp);

        //Recuperamos el contenido sacado en la Activity de Lista_Tareas y lo visualizamos en los EditText:
        idComp= getIntent().getIntExtra("idComp",0);
        idUsuario2= getIntent().getIntExtra("idUsuario2",0);
        titulo= getIntent().getStringExtra("titulo");
        descrip= getIntent().getStringExtra("descrip");
        hora= getIntent().getStringExtra("hora");
        fecha= getIntent().getStringExtra("fecha");
        nombre= getIntent().getStringExtra("nombre");

        idHora.setText(hora);
        idTitulo.setText(titulo);
        idDescrip.setText(descrip);
        idFecha.setText(fecha);
        idCompartida.setText(nombre);
        //OnClic para fecha y hora para poder ser modificados:
        idFecha.setOnClickListener(this);
        idHora.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.idFechaTaeaComp:
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
            case R.id.idHoraTareaComp:
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
    public void modificarTareasComp(View view){
        UrlModificar("http://192.168.1.131/ProyectoNuevo/AgendaCompartida/modificarTareaComp.php");
    }
    public void UrlModificar(String UrlModificar){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlModificar, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){
                    //Recogemos la respuesta del PHP con el JSON:
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int respuesta= jsonObject.getInt("respuesta");
                        //A) Si la respuesta es igual a 1 lanzamos Toast que la tarea a sido modificada:
                        if (respuesta == 1){
                            Toast.makeText(TareaCompartida.this, "La Tarea ha sido modificada", Toast.LENGTH_SHORT).show();
                            //Lanzamos a la activity de ListaTareas para ver si el contenido se ha modificado:
                            Intent intent = new Intent(getApplicationContext(),Lista_tareas.class);
                            intent.putExtra("idComp",idComp);
                            intent.putExtra("idUsuario2",idUsuario2);
                            startActivity(intent);
                            finish();

                            //B) La conexión con la base de datos es erronia, lanzaremos el mensaje para que le usurio sepa que hay un problema.
                        }else if (respuesta == 0){
                            Toast.makeText(TareaCompartida.this, "Error de conexion con la base de datos, intentelo mas tarde", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(TareaCompartida.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Mandamos al PHP lo modificado:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idComp", String.valueOf(idComp));
                parametros.put("titulo", idTitulo.getText().toString());
                parametros.put("descrip", idDescrip.getText().toString());
                parametros.put("hora", idHora.getText().toString());
                parametros.put("fecha", idFecha.getText().toString());
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    //Boton que conecta con la URL de PHP para elimnar la tarea:
    public void eliminarComp(View view){
        URLeliminar("http://192.168.1.131/ProyectoNuevo/AgendaCompartida/eliminarTareasComp.php");
    }

    public void URLeliminar(String Url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){
                    try {
                        //Recogemos la respuesta del PHP con el JSON:
                        JSONObject jsonObject = new JSONObject(response);
                        int respuesta= jsonObject.getInt("respuesta");

                        //A) Si la respuesta es uno elimanamos la tarea: y lanzamos el Toast indicandolo
                        if (respuesta == 1){
                            Toast.makeText(TareaCompartida.this, "Tarea eliminada", Toast.LENGTH_SHORT).show();

                            //Lanzamos a la activity de ListaTareas para ver si el contenido esta borrado:
                            Intent intent = new Intent(getApplicationContext(),Lista_tareas.class);
                            intent.putExtra("idComp",idComp);
                            intent.putExtra("idUsuario2",idUsuario2);
                            startActivity(intent);
                            finish();

                            //B) La conexión con la base de datos es erronia, lanzaremos el mensaje para que le usurio sepa que hay un problema.
                        }else if (respuesta == 0){
                            Toast.makeText(TareaCompartida.this, "Error de conexion con la base de datos, intentelo mas tarde", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(TareaCompartida.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Mandamos el idComp al PHP para que elimine esa tarea en concreto:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idComp", String.valueOf(idComp));
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    //Volvemos atras:
    public void Vlistatareas(View view){
        Intent intent = new Intent(TareaCompartida.this, Lista_tareas.class);
        intent.putExtra("idUsuario2",idUsuario2);
        startActivity(intent);
    }
}
