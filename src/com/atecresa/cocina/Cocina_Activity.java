package com.atecresa.cocina;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.comunicaciones.servicios.CocinaService;
import com.atecresa.notificaciones.Notifica;
import com.atecresa.preferencias.Constantes;
import com.atecresa.preferencias.PreferenciasManager;
import com.atecresa.preferencias.Sistema;
import com.atecresa.preferencias.TpvConfig;
import com.atecresa.util.DesignM;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

@SuppressLint("ResourceAsColor")
public class Cocina_Activity extends AppCompatActivity implements
        TextToSpeech.OnInitListener, androidx.appcompat.app.ActionBar.OnNavigationListener {

    private androidx.appcompat.app.ActionBar actionBar;

    private LinearLayout layoutListas;

    private ListView listaAcumulados;

    private ArrayList<ListView> listasCocina;
    private ArrayList<TextView> listaTiempos;

    private EjecutarFuncion ef;

    private boolean segundoPlano = false;

    private TextToSpeech tts;

    private Ad_linea_acumulados_cocina ada;

    // PARA DROPDOWN
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    // ACUMULADOS
    private TextView txTituloAcumulado;

    // PARA CONTROLAR QUE NO SE REPITAN OPERACIONES INNECESARIAS
    private boolean inicio = false;

    //DISEÑO APP
    ColorDrawable colorFondoApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cocina_content_v2);

        // NUEVO CÓDIGO PARA MANTENER LA PANTALLA ACTIVA SIN QUE EL DISPOSITIVO
        // ENTRE EN REPOSO
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Inicializamos la lista de celdas con el tiempo
        this.listaTiempos = new ArrayList<>();

        // booleano para no cargar dos veces las comandas
        inicio = true;

        // VOZ
        tts = new TextToSpeech(this, this);

        //COLOR FONDO
        colorFondoApp = new ColorDrawable();
        // ACTION BAR
        if (getSupportActionBar() != null) {
            actionBar = getSupportActionBar();
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            actionBar.setHomeButtonEnabled(true);
            colorFondoApp.setColor(TpvConfig.getAppBackColor());
            final String[] dropdownValues = getResources().getStringArray(
                    R.array.action_cocina_mesas);
            // Specify a SpinnerAdapter to populate the dropdown list.
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    actionBar.getThemedContext(),
                    android.R.layout.simple_spinner_item, android.R.id.text1,
                    dropdownValues);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Set up the dropdown list menu_comanda_buttonbar in the action bar.
            actionBar.setListNavigationCallbacks(adapter, this);
        }

        // ***************

        ConstraintLayout layoutBase = findViewById(R.id.layout_base_cocina);
        layoutBase.setBackgroundColor(TpvConfig.getAppBackColor());

        layoutListas = findViewById(R.id.layoutMesasCocina);

        listaAcumulados = findViewById(R.id.listaAcumulados);
        View header = getLayoutInflater().inflate(
                R.layout.item_header_cocina_acumulado_v3, null);

        txTituloAcumulado = header
                .findViewById(R.id.txt_titulo_cocina_acumulado);
        txTituloAcumulado.setText(R.string.txt_pendiente);
        txTituloAcumulado.setTextSize(PreferenciasManager.getTamFuenteNew());
        this.listaAcumulados.addHeaderView(header);

        this.listasCocina = new ArrayList<>();

        try {
            mostrarComandas(getSupportActionBar().getSelectedNavigationIndex());

        } catch (Exception e) {
            Log.e("ACTIVITY COCINA", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
        }

        registerReceiver(broadcastReceiverCocina, new IntentFilter(CocinaService.BROADCAST_ACTION));
        //intentServicioCocina = new Intent(this, ServicioSocketCnx.class);
        intentServicioCocina = new Intent(this, CocinaService.class);

        try {
            /*
            todo VAMOS A PROBAR OTRO TIPO DE SERVICIO EN PRINCIPIO MÁS PERSISTENTE
             */
            startService(intentServicioCocina);
            //bindService(intentServicioCocina,);

        } catch (Exception e) {
            Log.e("ACTIVITY COCINA", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
        }
        handler = new Handler();
        handler.postDelayed(runnable, intervalo);

        //TODO Ver si finalmente implmentamos este handler
       // handlerServerSocket=new Handler();
        //handlerServerSocket.postDelayed(rebootServer,600000); //REINICIAMOS SERVER SOCKET CADA 10 MIN

        // NOTIFICACIÓN NUEVA

        notificarEstadoConfig();

        //FUENTES
        PreferenciasManager.cargarPreferencias();
    }

    @Override
    protected void onResume() {
        Inicio.setContext(Cocina_Activity.this);
        //TODO PRUEBA
        configurarActionBar();
        super.onResume();

    }

    // RESTAURAR VALORES
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM) && getSupportActionBar() != null) {

            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    // GUARDAR INSTANCIA
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        super.onSaveInstanceState(outState);
        if (getSupportActionBar() != null)
            outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {

        if (!inicio) {
            actualizarUI();
        } else
            inicio = false;
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cocina, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (listaAcumulados.getVisibility() == View.VISIBLE)
                    listaAcumulados.setVisibility(View.GONE);
                else
                    listaAcumulados.setVisibility(View.VISIBLE);
                return true;
            case R.id.action_cocina_notificar:
                if (Inicio.gb.getCocina().getListaLineasSinNotificar().size() > 0) {
                    segundoPlano = false;
                    ef = new EjecutarFuncion();
                    ef.execute(Constantes.notificar_platos);
                } else {
                    Notifica.mostrarToastSimple(Cocina_Activity.this,
                            getString(R.string.txt_toast_cocina));
                }
                return true;
            case R.id.action_cocina_recargar:
                actualizarComandas();
                return true;
            case R.id.action_cocina_mute:
                if (tts != null)
                    tts.stop();
                return true;
            case R.id.action_cocina_salir:
                salir();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void actualizarComandas() {
        segundoPlano = false;
        ef = new EjecutarFuncion();
        ef.execute(Constantes.actualizar_platos);
    }

    private void notificarEstadoConfig() {
        try {
            if (Inicio.gb.isImpresoraConfiguradaEnTPV()) {
                Notifica.mostrarToast(Cocina_Activity.this,
                        getString(R.string.mensaje_impresora_no_configurada), 2,
                        Toast.LENGTH_LONG);
            }
        } catch (Exception e) {
            Log.e("ACTIVITY COCINA", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
        }
    }

    //region LISTAS DE COMANDAS
    //Función que dibuja las comandas en pantalla
    private void mostrarComandas(int modo) {

        for (int i = 0; i < Inicio.gb.getCocina().getListaComandaCocinas().size(); i++) {
            ComandaCocina miComandaCocina = Inicio.gb.getCocina().getListaComandaCocinas()
                    .get(i);
            switch (modo) {
                case Constantes.ver_mesas_en_curso:
                    try {
                        if (!miComandaCocina.isCompleta())
                            _mostrarComanda(miComandaCocina, i);
                    } catch (Exception e) {
                        Log.e("ACTIVITY COCINA", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
                    }
                    break;
                case Constantes.ver_mesas_finalizadas:
                    if (miComandaCocina.isCompleta())
                        _mostrarComanda(miComandaCocina, i);
                    break;
                case Constantes.ver_mesas_todas:
                    _mostrarComanda(miComandaCocina, i);
                    break;
            }
        }

        new ActualizarListaAcumulados().execute();
    }

    private void _mostrarComanda(ComandaCocina com, int position) {

        Ad_linea_comanda_cocina acc;
        this.listaTiempos.clear();
        if (com.getLineasComanda().size() > 0) {
            /*
             * REVISAR ESTA LINEA
             */
            //com.ordenarLineasComanda();


            //ENCABEZADO DE LA LISTA
            View header;
            //PARA VERSIONES ANTIGUAS. HEADER SIN CARDVIEW
            if (Sistema.usarElementosVisualesCompatibles()) {
                header = getLayoutInflater().inflate(R.layout.item_header_cocina_compatible, null);
            }else{
                header = getLayoutInflater().inflate(R.layout.item_header_cocina, null);
            }

            TextView txNumMesa = header
                    .findViewById(R.id.txt_titulo_cocina);
            TextView tvCamarero = header
                    .findViewById(R.id.txt_subtitulo_cocina);
            TextView txtRango = header
                    .findViewById(R.id.txt_rango_cocina);

            final String numMesa = com.getMesa();
            JSONObject rango = Inicio.gb
                    .getRangoMesa(Integer.parseInt(numMesa));

            header.setTag(position); // POSICI�N DE LA COMANDA DENTRO DE LA
            // LISTA DE COMANDAS

            header.setOnClickListener(v -> {
                int pos = (Integer) v.getTag();
                try {
                    if (tts.isSpeaking())
                        hablar(Inicio.gb.getCocina().getVozContenidoMesa(
                                pos), TextToSpeech.QUEUE_ADD, 1);
                    else
                        hablar(Inicio.gb.getCocina().getVozContenidoMesa(
                                pos), TextToSpeech.QUEUE_ADD, 0);
                } catch (Exception e) {
                    Log.e("ACTIVITY COCINA", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
                }
            });

            try {
                txtRango.setText(rango.getString("TX").toUpperCase()); // Si no hay rango, esta aparcada sin rango, usamos sus valores a 0
            } catch (Exception e) {
                txtRango.setText("");
            }
            txtRango.setTextSize(PreferenciasManager.getTamFuenteNew());
            txNumMesa.setText(numMesa);
            txNumMesa.setTextSize(PreferenciasManager.getTamTituloNew());

            String comensales = com.getComensales();
            if (!comensales.equals("0"))
                comensales = "Comensales: " + com.getComensales() + "\n";
            else
                comensales = "";
            tvCamarero.setText(comensales + " " + com.getOperador().toUpperCase());

            tvCamarero.setTextSize(PreferenciasManager.getTamFuenteNew());

            int colorFondo;
            int colorFuente;

            try {
                colorFondo = DesignM.genColor(Integer.parseInt(rango
                        .getString("BACKC")));
                colorFuente = DesignM.genColor(Integer.parseInt(rango
                        .getString("FOREC")));
                txtRango.setBackgroundColor(colorFondo);
                txtRango.setTextColor(colorFuente);

                txNumMesa.setBackgroundColor(colorFondo);
                txNumMesa.setTextColor(colorFuente);

                tvCamarero.setBackgroundColor(colorFondo);
                tvCamarero.setTextColor(colorFuente);
            } catch (Exception e1) {
                Log.e("ACTIVITY COCINA", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e1.getMessage());
            }

            /*
             * Cronómetro
             */

            TextView celdaTiempo = header.findViewById(R.id.c1);
            celdaTiempo.setTextSize(PreferenciasManager.getTamFuenteNew());

            try {
                if (!com.isCompleta()) {
                    switch (com.getTiempoEspera()) {
                        case Constantes.tiempo_ok:
                            celdaTiempo.setBackgroundColor(Color.GREEN);
                            break;
                        case Constantes.tiempo_con_retraso:
                            celdaTiempo.setTextColor(Color.BLACK);
                            celdaTiempo.setBackgroundColor(Color.YELLOW);
                            break;
                        case Constantes.tiempo_con_retraso_grave:
                            celdaTiempo.setTextColor(Color.WHITE);
                            celdaTiempo.setBackgroundColor(Color.RED);
                            break;
                    }
                    celdaTiempo.setText(com.getTiempo().toUpperCase());
                } else {
                    celdaTiempo.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                Log.e("ACTIVITY COCINA", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
            }

            acc = new Ad_linea_comanda_cocina(this, com.getLineasComanda(),
                    position);

            //CAMBIOS YA PARA EL HILO PRINCIPAL
            this.listaTiempos.add(celdaTiempo);
            ListView lv = new ListView(this);
            lv.addHeaderView(header);
            lv.setAdapter(acc);
            lv.setMinimumWidth(PreferenciasManager.getAnchoColumnasCocina());
            lv.setMinimumHeight(PreferenciasManager.getAltoColumnasCocina());
            lv.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            lv.setOnItemClickListener(listenerComandas);
            //Nuevo cambio divider
            lv.setDividerHeight(DesignM.divider_size);
            lv.setBackground(colorFondoApp);
            layoutListas.addView(lv);
            this.listasCocina.add(lv);
        }

    }


    //LISTENER PARA TODAS LAS LISTAS, PARA ACTUALIZAR EL ADAPTADOR

    private final AdapterView.OnItemClickListener listenerComandas = (adapterView, v, position, id) -> {
        //ada.notifyDataSetChanged(); //Esto por ahora comentado. No entiendo para que lo hago aquí
    };

    private void borrarListasComandas() {
        this.layoutListas.removeAllViews();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //endregion

    //region LISTA DE ACUMULADOS

    //NUEVO PROCEDIMIENTO EN ASÍNCRONO PARA PODER CARGAR ESTA LISTA A PARTE DE LAS COMANDAS
    private class ActualizarListaAcumulados extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Inicio.gb.getCocina().generarListaAcumulados();
            ada = new Ad_linea_acumulados_cocina(Cocina_Activity.this, Inicio.gb.getCocina().getLineasAcumuladas());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            listaAcumulados.setAdapter(ada);
            listaAcumulados.setDividerHeight(DesignM.divider_size);
            txTituloAcumulado.setOnClickListener(v -> {
                try {
                    if (tts.isSpeaking())
                        hablar(Inicio.gb.getCocina().getVozAcumulados(),
                                TextToSpeech.QUEUE_ADD, 1);
                    else
                        hablar(Inicio.gb.getCocina().getVozAcumulados(),
                                TextToSpeech.QUEUE_ADD, 0);
                } catch (Exception e) {
                    Log.e("ACTIVITY COCINA", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
                }
            });
            super.onPostExecute(aVoid);
        }
    }

    //endregion

    private class EjecutarFuncion extends AsyncTask<Integer, Void, Void> {

        int funcion = 0;

        @Override
        protected Void doInBackground(Integer... params) {
            Inicio.gb.cargarJSONFunciones();
            funcion = params[0];
            switch (funcion) {
                case Constantes.notificar_platos:
                    Inicio.gb.getCocina().notificarPlatosPreparados();
                    Inicio.gb.cargarMesasCocina();
                    Inicio.gb.cargarConfigMesas();
                    break;
                case Constantes.actualizar_platos:
                    Inicio.gb.cargarMesasCocina();
                    Inicio.gb.cargarConfigMesas();
                    break;
                case Constantes.enviar_nserie:
                    Inicio.gb.cargarJSONFunciones();
                    break;
                case Constantes.actualizar_acumulados:
                    Inicio.gb.getCocina().generarListaAcumulados();
                    break;
            }
            Inicio.gb.ejecutarfuncion();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            segundoPlano = false;
            switch (funcion) {
                case Constantes.notificar_platos:
                    //EL TPV DEVUELVE ERROR AUN HACIENDO BIEN LA OPERAC
                    if (Inicio.gb.getResultado().equals("Ok")) {
                        Toast.makeText(getApplicationContext(), "Registros enviados", Toast.LENGTH_LONG).show();
                        Inicio.gb.getCocina().vaciarListaLineasSinNotificar();
                    } else {
                        Notifica.mostrarMensajeEstado(Cocina_Activity.this);
                    }
                    actualizarUI();
                    break;
                case Constantes.actualizar_platos:
                    actualizarUI();
                    break;
                case Constantes.actualizar_acumulados:
                    ada = new Ad_linea_acumulados_cocina(Cocina_Activity.this, Inicio.gb
                            .getCocina().getLineasAcumuladas());
                    listaAcumulados.setAdapter(ada);
                    break;
            }


        }
    }

    /*
     * Función que actualiza toda la interfaz
     */

    //todo prueba para manipular el actionbar después de carga de información
    private void configurarActionBar() {
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(TpvConfig.getAppBackColor());
        actionBar.setBackgroundDrawable(colorDrawable);
    }

    private void actualizarUI() {
        borrarListasComandas();
        if (getSupportActionBar() != null)
            mostrarComandas(getSupportActionBar().getSelectedNavigationIndex());
        new ActualizarListaAcumulados().execute();
    }



    private Intent intentServicioCocina;

    private final BroadcastReceiver broadcastReceiverCocina = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra("mensaje", 0)) {
                case Constantes.mensaje_actualizacion_cocina:
                    actualizarUI();
                    //FILTRAMOS AQUÍ POSIBLE ERROR DE LAYOUT
                    /*
                    if (intent.getBooleanExtra("notificar", false)) {
                        Notifica.notificar(Cocina_Act.this, getString(R.string.txt_nueva_comanda_recibida),
                                getString(R.string.txt_comanda_recibida_en_cocina), getString(R.string.txt_comanda_recibida));
                    }
                    */
                    break;
                case Constantes.mensaje_actualizacion_acumulados:
                    //actualizarListaAcumulados();
                    new ActualizarListaAcumulados().execute();
                    //la diferencia entre si es necesario actualizar siempre o lo dejamos comentado
                    //actualizarUI();
                    break;
            }
        }
    };

    private final int intervalo = 180000; // 3 MIN -> AMPLIAMOS INTERVALO PARA ACTIVAR SOCKET
    private Handler handler;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //activarServicio(); //POR AHORA NO HACEMOS ESTA IMPLEMENTACIÓN EN EL APARTADO DE COMANDERO VA BIEN
            try {

                if (Inicio.gb.getCocina().hayPlatosSinNotificar()) {
                    ef = new EjecutarFuncion();
                    ef.execute(Constantes.actualizar_platos);
                } else {
                    Inicio.gb.getCocina().actualizarFechas();
                    //actualizarUI(); No toco la interfaz por si acaso
                }
            } catch (Exception e) {
                Log.e("ACTIVITY COCINA", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
            }
            handler.postDelayed(this, intervalo);
        }
    };


    //endregion

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                salir();
            case KeyEvent.KEYCODE_HOME:
                // return false;
        }
        return false;
    }

    @Override
    protected void onDestroy() {


        try {
            unregisterReceiver(broadcastReceiverCocina);
            stopService(intentServicioCocina); //no se si Android para por si solo el servicio
        } catch (Exception e) {
            Log.e("ACTIVITY COCINA", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
        }

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }


        if (handler != null)
            handler.removeCallbacks(runnable); //Esto seguro que no funciona

        super.onDestroy();
    }

    private void salir() {
        //SI HAY LINEAS SIN NOTIFICAR O EN PREPARACIÓN, PREGUNTAMOS SI QUEREMOS SALIR
        if (Inicio.gb.getCocina().hayLineasSinNotificar()|| Inicio.gb.getCocina().hayPlatosEnPreparacion()) {
            AlertDialog.Builder alert = new AlertDialog.Builder(Cocina_Activity.this);

            alert.setTitle(getString(R.string.dialog_titulo_salir_cocina));
            alert.setMessage(getString(R.string.dialog_mensaje_salir_cocina));

            alert.setPositiveButton(getString(R.string.dialog_ok),
                    (dialog, whichButton) -> {
                        Inicio.gb.getCocina()
                                .vaciarListaLineasSinNotificar();
                        Inicio.gb.getCocina()
                                .vaciarLineasPlatosEnPreparacion();
                        finish();
                    });

            alert.setNegativeButton(getString(R.string.dialog_cancelar),
                    (dialog, whichButton) -> {
                        // Canceled.
                    });

            alert.show();
        } else {
            finish();
        }
    }

    private void hablar(String sMessage, int intQueueType, int delay) {
        if (tts == null || sMessage == null)
            return;
        sMessage = sMessage.trim();
        // isSpeaking = true;
        if (delay > 0) {
            tts.playSilence(delay, intQueueType, null);
            intQueueType = TextToSpeech.QUEUE_ADD;
        }
        tts.speak(sMessage, intQueueType, null);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            // Esto será configurable Esto si funciona, probar con los
            // idiomas!!!! configurable!!!

            // tts.setLanguage(Locale.FRANCE);
            // int result = 0;

            //POR DEFECTO MYGEST NORMAL
            //Locale loc = new Locale("ES", "", "");

            //MYGEST 2016
            Locale loc = Locale.getDefault();

            int result = tts.setLanguage(loc);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Idioma no disponible");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    /*
     * CREAR FUNC QUE NOS DIGA SI HAY ALGÚN PLATO SIN MODIFICAR
     * COMPROBAR SI ESTO TIRA DEL SERVICIO WEB
     */


}
