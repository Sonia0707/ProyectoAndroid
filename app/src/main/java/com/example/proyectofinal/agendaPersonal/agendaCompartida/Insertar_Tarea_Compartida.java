package com.example.proyectofinal.agendaPersonal.agendaCompartida;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.proyectofinal.agendaPersonal.Insertar_Tarea;
import com.example.proyectofinal.agendaPersonal.TareasPersonales;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Insertar_Tarea_Compartida extends AppCompatActivity implements View.OnClickListener {
    //Crear varibles para visualizar y las que necesitemos:
    EditText idtitulo, idFecha, idHora, idDescrip;
    Button btnGuardarComp;
    ImageView atrasNuevaComp;
    String nombre, fecha, horas,descrip;
    int idUsuario,idUsuario2,idComp;
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
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar__tarea__compartida);

        idtitulo = (EditText) findViewById(R.id.idTituloComp);
        idFecha = (EditText) findViewById(R.id.fechComp);
        idHora = (EditText) findViewById(R.id.idhhComp);
        idDescrip = (EditText) findViewById(R.id.idDescComp);
        btnGuardarComp = (Button) findViewById(R.id.btnGuardarComp);
        atrasNuevaComp = (ImageView) findViewById(R.id.atrasNuevaComp);

        //Sharpreferens idUsuario:
        SharedPreferences preferences = getSharedPreferences("LoginUsuario", Context.MODE_PRIVATE);
        idUsuario = preferences.getInt("idUsuario", 0);

        //Recuperamos el idUsuario2 de la otra actividad al clicar en el amigo:
        idUsuario2= getIntent().getIntExtra("idUsuario2",0);

        //Crear OnClick para cada objeto que sea necesario:
        btnGuardarComp.setOnClickListener(this);
        idFecha.setOnClickListener(this);
        idHora.setOnClickListener(this);
        atrasNuevaComp.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //Al pulsar el boton de login comprueba si los campos estan vacios
            // y si no es así manda la URL al método validar usurios de la clase Volley.
            case R.id.btnGuardarComp:
                nombre = idtitulo.getText().toString();
                fecha = idFecha.getText().toString();
                horas = idHora.getText().toString();
                descrip = idDescrip.getText().toString();

                if (!nombre.isEmpty() && !descrip.isEmpty()) {
                    if (!fecha.isEmpty() && !horas.isEmpty() ){
                        insertarTareaComp("http://192.168.1.131/ProyectoNuevo/AgendaCompartida/InsertarContenido.php?");
                    }else {
                        insertarTareaComp2("http://192.168.1.131/ProyectoNuevo/AgendaCompartida/InsertarContenido2.php?");
                    }

                } else {
                    Toast.makeText(this, "No se permiten campos vacios", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.fechComp:
                //DatePickerDialog de la fecha Para que al usuario le sea mas facil introducirla:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                        final int mesActual = month + 1;
                        //Formateo el día obtenido: antepone el 0 si son menores de 10
                        String diaFormateado = (dayOfMonth < 10) ? CERO + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                        //Formateo el mes obtenido: antepone el 0 si son menores de 10
                        String mesFormateado = (mesActual < 10) ? CERO + String.valueOf(mesActual) : String.valueOf(mesActual);
                        //Muestro la fecha con el formato deseado
                        idFecha.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);
                    }
                }, anio, mes, dia);
                //Lo lanzamos
                datePickerDialog.show();
                break;
            case R.id.idhhComp:
                //TimePickerDialog de la hora Para que al usuario le sea mas facil introducirla:
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
                }, hora,minuto,false);
                //Lo lanzamos:
                timePickerDialog.show();
                break;
            case R.id.atrasNuevaComp:
                Intent intent = new Intent(Insertar_Tarea_Compartida.this, Lista_tareas.class);
                intent.putExtra("idUsuario2",idUsuario2);
                startActivity(intent);
                break;
        }
    }
    public void insertarTareaComp(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){
                    try {
                        //Respuesta del JSON de PHP:
                        JSONObject jsonObject = new JSONObject(response);
                        int respuesta= jsonObject.getInt("respuesta");
                        //Si la respuesta es uno quiere decir que la conexion ha ido bien y se ha insertado en la tabla:
                        if (respuesta == 1){
                            Toast.makeText(Insertar_Tarea_Compartida.this, "Insertado Contenido", Toast.LENGTH_SHORT).show();
                            //Lanzamos a la activity de Lista_Tareas para ver si el contenido esta metido:
                            Intent intent = new Intent(getApplicationContext(),Lista_tareas.class);
                            intent.putExtra("idUsuario2",idUsuario2);
                            startActivity(intent);
                            finish();
                            //B) La conexión con la base de datos es erronia, lanzaremos el mensaje para que le usurio sepa que hay un problema.
                        }else if (respuesta == 0){
                            Toast.makeText(Insertar_Tarea_Compartida.this, "Error de conexion con la base de datos, intentelo mas tarde", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Insertar_Tarea_Compartida.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Pasamos los parametros modificados al PHP para que este los modifique en la tabla:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idUsuario", String.valueOf(idUsuario));
                parametros.put("idUsuario2", String.valueOf(idUsuario2));
                parametros.put("fecha",fecha);
                parametros.put("titulo",nombre);
                parametros.put("descrip",descrip);
                parametros.put("hora",horas);
                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public void insertarTareaComp2(String URL){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){
                    try {
                        //Respuesta del JSON de PHP:
                        JSONObject jsonObject = new JSONObject(response);
                        int respuesta= jsonObject.getInt("respuesta");
                        //Si la respuesta es uno quiere decir que la conexion ha ido bien y se ha insertado en la tabla:
                        if (respuesta == 1){
                            Toast.makeText(Insertar_Tarea_Compartida.this, "Insertado Contenido", Toast.LENGTH_SHORT).show();
                            //Lanzamos a la activity de Lista_Tareas para ver si el contenido esta metido:
                            Intent intent = new Intent(getApplicationContext(),Lista_tareas.class);
                            intent.putExtra("idUsuario2",idUsuario2);
                            startActivity(intent);
                            finish();
                            //B) La conexión con la base de datos es erronia, lanzaremos el mensaje para que le usurio sepa que hay un problema.
                        }else if (respuesta == 0){
                            Toast.makeText(Insertar_Tarea_Compartida.this, "Error de conexion con la base de datos, intentelo mas tarde", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Insertar_Tarea_Compartida.this,"El sitio web no esta en servicio intentelo mas tarde.", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Pasamos los parametros modificados al PHP para que este los modifique en la tabla:
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idUsuario", String.valueOf(idUsuario));
                parametros.put("idUsuario2", String.valueOf(idUsuario2));
                parametros.put("titulo",nombre);
                parametros.put("descrip",descrip);

                //Despues retornamos toda la colección de datos mediante la instancia creada:
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}
