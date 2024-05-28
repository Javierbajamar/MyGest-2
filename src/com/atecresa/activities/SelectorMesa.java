package com.atecresa.activities;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBar.Tab;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentTransaction;

import com.atecresa.adaptadoresUIComanda.Ad_combo_impresoras;
import com.atecresa.adaptadoresUIComanda.Ad_grid_mesas;
import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.comunicaciones.servicios.ServicioSocketCnx;
import com.atecresa.comunicaciones.v3.CnxResponseK;
import com.atecresa.comunicaciones.v3.ListenerCnx;
import com.atecresa.gestionLineasComanda.GestorLineas;
import com.atecresa.notificaciones.Notifica;
import com.atecresa.notificaciones.Notify;
import com.atecresa.preferencias.Constantes;
import com.atecresa.preferencias.PreferenciasManager;
import com.atecresa.preferencias.TpvConfig;
import com.atecresa.print.DownloadTicket;
import com.atecresa.print.Print_Bluetooth;
import com.atecresa.util.DesignM;
import com.atecresa.util.Formateador;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

public class SelectorMesa extends AppCompatActivity implements
        TextToSpeech.OnInitListener {

    //region 1 VARIABLES
    private GridView gridMesas;
    private Ad_grid_mesas adGridMesas;

    private int positionRango = 0;

    private ActionBar actionBar;

    private Boolean isCargandoComanda = false;

    private Intent intent;
    private boolean segundoPlano = true;

    private TextToSpeech tts;

    private boolean inicio = false;

    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selector_mesa_activity);
        Inicio.setContext(SelectorMesa.this);


        inicio = true;
        // FUNCIÓN DE CONTROL PARA EVITAR NULL
        if (Inicio.gb == null)
            finish();

        //Inicializar sólo si tenemos activos los comandos de voz
        if (PreferenciasManager.usarComandoVoz())
            tts = new TextToSpeech(this, this); // PRUEBA DE TEXTO HABLADO

        // GRID DE LAS MESAS
        gridMesas = findViewById(R.id.gridMesas);
        gridMesas.setNumColumns(Integer.parseInt(PreferenciasManager.getNumColumnas()));

        // ponemos el color por defecto al fondo
        LinearLayout layoutBase = findViewById(R.id.layout_selector_mesa);
        //layoutBase.setBackgroundColor(TpvConfig.getAppBackColor()); //TODO CONTROLAR CAMBIO VISUAL

        // ACTIONBAR
        try {
            if (getSupportActionBar() != null) {
                actionBar = getSupportActionBar();
                actionBar.setHomeButtonEnabled(true);
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                actionBar.setSubtitle(Inicio.gb.getOperadorActual().getNombre().toUpperCase());
                actionBar.setDisplayHomeAsUpEnabled(true);
                String idt = Inicio.gb.getIdTerminal();
                if (!idt.equals(""))
                    actionBar.setTitle(idt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // LLENAMOS TABS
        if (Inicio.gb != null && Inicio.gb.getlistaRangos() != null) {
            for (int i = 0; i < Inicio.gb.getlistaRangos().size(); i++) {
                try {
                    String nombreRango = Inicio.gb.getlistaRangos().get(i)
                            .getString("TX");

                    Tab tab = actionBar.newTab().setText(nombreRango)
                            .setTabListener(new TabListener(this, nombreRango));

                    /* colores */
                    tab.setTabListener(new TabListener(this, nombreRango));
                    tab.setCustomView(R.layout.item_tab_mesas);

                    TextView txtTabMesa = tab.getCustomView()
                            .findViewById(R.id.txt_titulo_tab);
                    txtTabMesa.setText(nombreRango);

                    int colorFondo = DesignM.genColor(Integer
                            .parseInt(Inicio.gb.getlistaRangos().get(i)
                                    .getString("BACKC")));

                    int colorFuente = DesignM.genColor(Integer
                            .parseInt(Inicio.gb.getlistaRangos().get(i)
                                    .getString("FOREC")));

                    tab.getCustomView().setBackgroundColor(colorFondo);
                    //
                    txtTabMesa.setBackgroundColor(colorFondo);
                    txtTabMesa.setTextColor(colorFuente);

                    actionBar.addTab(tab);
                } catch (Exception e) {
                    Log.e("ACTIVITY SELECTOR MESA",
                            "Linea "
                                    + Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber() + ": "
                                    + e.getMessage());
                }
                configurarActionBar();

            }
        } else {
            Notifica.mostrarToast(this,
                    getString(R.string.mensaje_error_conexion), 2,
                    Toast.LENGTH_LONG);
            finish();
        }

        intent = new Intent(this, ServicioSocketCnx.class);

        // LO DEJAMOS AQUÍ PARA QUE SE EJECUTE UNA SOLA VEZ
        //Lleva un try catch por nuevo java.lang.NullPointerException
        try {
            if (Inicio.gb.isMyorder()) {
                ConsultarPedidos cp = new ConsultarPedidos();
                cp.execute(); // en segundo plano para notificar
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        runService();

        //TODO Prueba para guardar en memoria lo que hay en la base de datos a través de un observer
        controlListasPendientes();


    }

    /*
     * Cuando volvemos a la activity Actualizar mesas aqu�
     */

    @Override
    public void onResume() {
        super.onResume();
        Inicio.setContext(SelectorMesa.this);
        Inicio.reiniciarContador(); // ponemos el autonumerico del idd a cero

        isCargandoComanda = false;

        Inicio.setPantallaActual(this.getClass().getName());

        if (isDatosCorrectos()) {
            if (!Inicio.gb.isMesasOK()) {
                new ActualizarInfoMesas().execute(this.positionRango);
            } else {
                //this.actualizarGridMesas();
                new cargarAdapterMesas().execute();
                Inicio.gb.setMesasOK(false);
            }
        } else {
            finish();
        }


    }



    private void configurarActionBar() {
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(TpvConfig.getAppBackColor());
        actionBar.setBackgroundDrawable(colorDrawable);


    }

    //region GRID MESAS Y APERTURA
    private final AdapterView.OnItemClickListener listenerGridMesas = (adapterView, v, position, id) -> {

        //VIGILAR CAMBIOS EN NOTIFICACIONES. SI FALLA, VOLVER A TOAST SIMPLES
        try {
            adapterView.setEnabled(false);
            if (Inicio.gb.getMesas().get(position).getString("ESTADO").equals("PAGANDO")) {
                //Notifica.mostrarToastSimple(Act_SelectorMesa.this,getString(R.string.mensaje_mesa_pagando));
                Notifica.mostrarToast(SelectorMesa.this, getString(R.string.mensaje_mesa_pagando), 2, Toast.LENGTH_SHORT);
            } else if (Inicio.gb.getMesas().get(position).getInt("IDOPERADORBLOQUEO") > 0) {
                //Notifica.mostrarToastSimple(Act_SelectorMesa.this,getString(R.string.mensaje_mesa_bloqueada));
                Notifica.mostrarToast(SelectorMesa.this, getString(R.string.mensaje_mesa_bloqueada), 2, Toast.LENGTH_SHORT);
            } else {
                PreferenciasManager.setFacturando(true);
                Inicio.gb.setMesa(v.getId() + "");
                new CargarComanda().execute(v.getId() + "");
            }
        } catch (Exception e) {
            Log.e("ACTIVITY SELECTOR MESA", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
        } finally {
            adapterView.setEnabled(true);
        }

    };


    //endregion


    /*
     * Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_selector_mesa, menu);
        menu.getItem(0).setVisible(Inicio.gb.isMyorder());
        menu.getItem(1).setVisible(PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_CLOUD));
        menu.getItem(2).setVisible(PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_CLOUD));
        return true;
    }

    // PILLAMOS EL CLICK DEL MENU
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_selectorMesa_pedidos:
                if (Inicio.gb.isMyorder()) {
                    segundoPlano = false;
                    new ConsultarPedidos().execute();
                }

                return true;
            case android.R.id.home:
                //activarVoz();
                finish();
                return true;
            case R.id.action_selectorMesa_x:
                new Imprimir().execute(Constantes.imprimir_cierre_x);
                return true;
            case R.id.action_selectorMesa_z:

                android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(
                        SelectorMesa.this);

                alert.setTitle("CIERRE  DE TURNO");
                alert.setMessage("¿Desea cerrar el turno?");

                alert.setPositiveButton(getString(R.string.dialog_ok),
                        (dialog, whichButton) -> new Imprimir().execute(Constantes.imprimir_cierre_z));

                alert.setNegativeButton(getString(R.string.dialog_cancelar),
                        (dialog, whichButton) -> {
                            // Canceled.
                        });

                alert.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //ACTIVAMOS EL MODO VOZ CON EL BOTÓN DE LOS AURICULARES

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
            activarVoz();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }

    //region VOZ

    private void activarVoz() {
        if (PreferenciasManager.usarComandoVoz()){
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Hola ¿Qué desea hacer?");
            try {
                startActivityForResult(intent, Constantes.comando_voz);
            } catch (ActivityNotFoundException ignored) {

            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constantes.comando_voz) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String numMesa = Formateador.getNumString(result.get(0), result.get(1));
                ejecutarComandoVoz(result.get(0), numMesa);
            }
        }
    }

    private void ejecutarComandoVoz(String comando, String numMesa) {
        if (comando.contains("abrir") || comando.contains("abre")) {
            new CargarComanda().execute(numMesa);
        } else if (comando.contains("pedir") || comando.contains("cuenta")) {
            if (TpvConfig.hayImpresoras() && PreferenciasManager.seleccionarImpresoraPrefactura()) {
                mostrarSelectorImpresora(numMesa);
            } else {
                new ImprimirCuenta().execute(numMesa, "");

            }
        }
    }

    private AlertDialog alert;

    private void mostrarSelectorImpresora(String numMesa) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ListView listaImpresoras = new ListView(SelectorMesa.this);
        Ad_combo_impresoras ci = new Ad_combo_impresoras(SelectorMesa.this);

        listaImpresoras.setAdapter(ci);
        listaImpresoras
                .setOnItemClickListener((parent, view, position, id) -> {
                    //SI SELECCIONAMOS LA IMP BLUETOOTH
                    new ImprimirCuenta().execute(numMesa, TpvConfig.getImpresoras().get(position).getDispositivo_windows());
                    alert.dismiss();
                });

        builder.setTitle(getString(R.string.titulo_combo_impresoras));
        builder.setView(listaImpresoras);
        builder.setNegativeButton(getString(R.string.dialog_cancelar),
                (dialog, which) -> dialog.dismiss());
        alert = builder.create();
        alert.show();

    }


    @SuppressLint("StaticFieldLeak")
    class ImprimirCuenta extends AsyncTask<String, Void, Void> {

        String mesa = "";

        @Override
        protected Void doInBackground(String... params) {
            mesa = params[0];
            Inicio.gb.cargarJSONFunciones();
            if (!TpvConfig.hayImpresoras()) {
                Inicio.gb.pedirCuenta(mesa, "");
            } else {
                Inicio.gb.pedirCuenta(mesa, params[1]);
            }
            Inicio.gb.ejecutarfuncion();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (Inicio.gb.getResultado().equals("Ok")) {
                Notifica.mostrarToast(SelectorMesa.this, getString(R.string.mensaje_cuenta_pedida) + " " + mesa, 1, Toast.LENGTH_SHORT);
            } else {
                try {
                    Notifica.mostrarMensajeEstado(SelectorMesa.this);
                } catch (Exception e) {
                    Log.e("ACTIVITY COMANDA", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
                }
            }

        }
    }

    //endregion

    /*
     * LISTENER DE LAS TABS
     */

    private class TabListener implements ActionBar.TabListener {

        TabListener(AppCompatActivity activity, String tag) {
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            if (Inicio.hayRed()) {
                new ActualizarInfoMesas().execute(tab.getPosition());
                SelectorMesa.this.positionRango = tab.getPosition();
            } else {
                Notifica.mostrarToast(SelectorMesa.this, getString(R.string.mensaje_error_conexion), 2, Toast.LENGTH_LONG);
                //finish();
            }
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {

            try {
                if (!inicio) {
                    new ActualizarInfoMesas().execute(tab.getPosition());
                    SelectorMesa.this.positionRango = tab.getPosition();

                } else
                    inicio = false;
            } catch (Exception e) {
                Notifica.mostrarToast(SelectorMesa.this, getString(R.string.mensaje_error_conexion), 2, Toast.LENGTH_LONG);
                finish();
            }

        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {

        }
    }

    private class cargarAdapterMesas extends AsyncTask<Void, Void, Void> {

        final Parcelable state = gridMesas.onSaveInstanceState();


        @Override
        protected Void doInBackground(Void... voids) {
            adGridMesas = new Ad_grid_mesas(SelectorMesa.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adGridMesas.setNotifyOnChange(true);
            gridMesas.setAdapter(adGridMesas);
            gridMesas.setOnItemClickListener(listenerGridMesas);
            gridMesas.onRestoreInstanceState(state);
            super.onPostExecute(aVoid);
        }

    }

    /*
     * LLAMAMOS A TAREA ASÍNCRONA PARA ACTUALIZAR LA GRID DE MESAS CUANDO
     * CAMBIAMOS DE PESTAÑA
     */

    private class ActualizarInfoMesas extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            Inicio.gb.cargarJSONFunciones();
            Inicio.gb.cargarMesas(params[0], false);
            Inicio.gb.ejecutarfuncion();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            //PONEMOS ESTO AQUÍ A VER SI LOGRAMOS QUE NO SE REPITA EL CÓDIGO DE CARGA
            PreferenciasManager.setFacturando(false); //NUEVO BOOLEANO PARA EVITAR QUE LA GRID SE ACTULICE EN SEGUNDO PLANO
            try {
                if (Inicio.gb.getError() == Constantes.app_OK) {
                    if (Inicio.gb.isMesasCargadas()) {
                        //actualizarGridMesas();
                        new cargarAdapterMesas().execute();
                    } else {
                        Notifica.mostrarMensajeEstado(SelectorMesa.this);
                    }
                } else {
                    Notifica.mostrarToast(SelectorMesa.this,
                            getString(R.string.mensaje_error_conexion), 2,
                            Toast.LENGTH_LONG);
                    finish(); //TODO SUPERVISAR CAMBIO AQUÍ
                }
            } catch (Exception e) {
                finish();
            }

        }
    }


    /*
     * LLAMAMOS A TAREA ASÍNCRONA PARA CARGAR COMANDA
     */

    private class CargarComanda extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            Notifica.mostrarProgreso(SelectorMesa.this, "", "Cargando mesa ", true);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                Inicio.gb.cargarJSONFunciones();

                //Esto lo pasará a controlar el observer
                //if (Inicio.gb.hayLineasPendientes())
                //Inicio.gb.enviarPendiente();
                Inicio.gb.bloquearMesa(params[0]);
                Inicio.gb.cargarComanda(params[0]);
                Inicio.gb.ejecutarfuncion();
            } catch (Exception e) {
                //ERROR DE NULO DETECTADO EN GOOGLE PLAY
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Notifica.mostrarProgreso(SelectorMesa.this, "", "", false);
            if (Inicio.gb.isComandaOK()) {
                Intent comandaIntent = new Intent(SelectorMesa.this, ComandaActivityV3.class);
                startActivity(comandaIntent);
                isCargandoComanda = true;
            } else if (Inicio.gb.isMesaPagando()) {
                Notifica.mostrarToast(SelectorMesa.this,
                        getString(R.string.mensaje_mesa_pagando), 2,
                        Toast.LENGTH_SHORT);
            } else if (Inicio.gb.isMesaBloqueada()) {
                Notifica.mostrarToast(SelectorMesa.this,
                        getString(R.string.mensaje_mesa_bloqueada), 2,
                        Toast.LENGTH_SHORT);
            } else {
                Notifica.mostrarMensajeEstado(SelectorMesa.this);
                finish();
            }

        }
    }

    private boolean isDatosCorrectos() {
        return Inicio.gb != null;
    }

    //region SERVICIO SOCKET

    private void runService() {
        startService(intent);
        registerReceiver(broadcastReceiver, new IntentFilter(ServicioSocketCnx.BROADCAST_ACTION));
    }

    private void stopService() {
        unregisterReceiver(broadcastReceiver);
        stopService(intent);
    }

    // AQUÍ RECIBIMOS LOS MENSAJES DEL SERVICIO DE SOCKET
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra("mensaje", 0)) {
                case Constantes.mensaje_actualizacion_mesas:
                    //actualizarGridMesas();
                    new cargarAdapterMesas().execute();
                    break;
                case Constantes.mensaje_nuevo_pedido_myorder:
                    if (Inicio.gb.hayPedidosSinConfirmar())
                        if (!Inicio.getPantallaActual().equals(
                                "com.atecresa.activities.Act_SelectorPedidos"))
                            // notificarMyorder();
                            notificar(
                                    getString(R.string.notificacion_myorder_ticker),
                                    getString(R.string.notificacion_myorder_titulo),
                                    getString(R.string.notificacion_myorder_contenido),
                                    1, Constantes.notificacion_myorder);
                    break;
                case Constantes.mensaje_notificacion_nuevos_platos:
                    notificar(getString(R.string.notificacion_cocina_ticker),
                            getString(R.string.notificacion_cocina_ticker),
                            Inicio.gb.getMesasPreparadas(), 0,
                            Constantes.notificacion_plato_preparado);
                    break;
            }
        }
    };


    //endregion

    //region PEDIDOS

    private class ConsultarPedidos extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!segundoPlano)
                Notifica.mostrarProgreso(SelectorMesa.this, "",
                        getString(R.string.progreso_consulta_myorder), true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Inicio.gb.cargarJSONFunciones();
            Inicio.gb.consultarPedidosMyorder();
            Inicio.gb.ejecutarfuncion();
            return null;
        }

        @Override
        protected void onPostExecute(Void res) {

            try {
                if (!segundoPlano) {
                    Notifica.mostrarProgreso(SelectorMesa.this, "", "", false);
                    if (Inicio.gb.hayPedidosSinConfirmar()) {
                        Intent pedidosI = new Intent(SelectorMesa.this,
                                SelectorPedidos.class);
                        startActivity(pedidosI);
                    } else {
                        Notifica.mostrarToast(SelectorMesa.this,
                                getString(R.string.mensaje_no_hay_pedidos), 1,
                                Toast.LENGTH_SHORT);
                    }
                } else {
                    if (Inicio.gb.hayPedidosSinConfirmar()) {
                        notificar(getString(R.string.notificacion_myorder_ticker),
                                getString(R.string.notificacion_myorder_titulo),
                                getString(R.string.notificacion_myorder_contenido),
                                1, Constantes.notificacion_myorder);
                    }
                }
                segundoPlano = true;
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }
    }

    //endregion

    //region NOTIFICACIONES

    private void notificar(String ticker, String titulo, String mensaje,
                           int destino, int tipo_notificacion) {

        if (PreferenciasManager.usarComandoVoz()) {
            switch (tipo_notificacion) {
                case Constantes.notificacion_myorder:
                    if (tts.isSpeaking())
                        this.hablar(
                                getString(R.string.notificacion_myorder_contenido),
                                TextToSpeech.QUEUE_ADD, 1);
                    else
                        this.hablar(
                                getString(R.string.notificacion_myorder_contenido),
                                TextToSpeech.QUEUE_ADD, 0);

                    break;
                case Constantes.notificacion_plato_preparado:
                    if (tts.isSpeaking())
                        this.hablar(Inicio.gb.getMensajeVozNotificar(),
                                TextToSpeech.QUEUE_ADD, 1);
                    else
                        this.hablar(Inicio.gb.getMensajeVozNotificar(),
                                TextToSpeech.QUEUE_ADD, 0);
                    break;
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && tipo_notificacion == Constantes.notificacion_plato_preparado) {
            try {
                //NotificationHelper nh=new NotificationHelper(getApplicationContext());
                //nh.createNotification(titulo,mensaje);

                //PRUEBA DE CLASE EN KOTLIN
                Notify n = new Notify(getApplicationContext());
                n.showPushNotification(titulo, mensaje);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(
                        this).setSmallIcon(R.drawable.ic_launcher)
                        .setTicker(ticker).setContentTitle(titulo)
                        .setContentText(mensaje).setAutoCancel(true);

                if (PreferenciasManager.notificarSonido()) {
                    builder.setDefaults(Notification.DEFAULT_SOUND
                            | Notification.DEFAULT_VIBRATE
                            | Notification.DEFAULT_LIGHTS);
                } else {
                    builder.setDefaults(Notification.DEFAULT_VIBRATE
                            | Notification.DEFAULT_LIGHTS);
                }

                PendingIntent contentIntent;

                if (destino == 0) {
                    // EL DESTINO!!!!!!!
                    Intent notificationIntent = new Intent(); // ESTO SE SUPONE QUE
                    // NO HACE NADA
                    contentIntent = PendingIntent.getActivity(this, 0,
                            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                } else {
                    Intent notificationIntent = new Intent(this,
                            SelectorPedidos.class);
                    contentIntent = PendingIntent.getActivity(this, 0,
                            notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);// FLAG_UPDATE_CURRENT);
                    // //KIT
                    // KAT
                }

                builder.setContentIntent(contentIntent);
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(1, builder.build());
            } catch (Exception e) {
                Log.e("ACTIVITY SELECTOR MESA", "Linea "
                        + Thread.currentThread().getStackTrace()[2].getLineNumber()
                        + ": " + e.getMessage());
            }
        }


    }

    //endregion

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

            Locale loc = new Locale("ES", "", "");
            int result = tts.setLanguage(loc);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        // HEMOS MOVIDO ESTO AQUI
        // ANTES ESTABA EN EL ON STOP
        try {
            stopService();
        } catch (Exception e) {
            Log.e("ACTIVITY SELECTOR MESA", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
        super.onDestroy();
    }

    //region X y Z

    private class Imprimir extends AsyncTask<String, Void, Void> {

        String txtTicket = "";
        String opcion = "";

        @Override
        protected Void doInBackground(String... pam) {
            opcion = pam[0];
            switch (opcion) {
                case Constantes.imprimir_cierre_x:
                    txtTicket = DownloadTicket.INSTANCE.getX();
                    break;
                case Constantes.imprimir_cierre_z:
                    txtTicket = DownloadTicket.INSTANCE.setZ();
                    break;
                case "getZ":
                    //txtTicket = Download_Ticket.setZ(); //TODO FUNCIÓN NUEVA QUE RECIBA EL ID
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (PreferenciasManager.isPrefacturaBluetooth()) {
                if (!new Print_Bluetooth().print(txtTicket)) {
                    Toast.makeText(SelectorMesa.this, R.string.msg_imprimir_via_bluetooth, Toast.LENGTH_LONG).show();
                    abrirVisorPantalla(opcion, txtTicket);
                }
            } else {
                abrirVisorPantalla(opcion, txtTicket);
            }
        }
    }

    private void abrirVisorPantalla(String tipoDoc, String contenido) {
        Intent v = new Intent(SelectorMesa.this, Visor_Docs.class);
        contenido=contenido.replace("                                        \n" +
                "                                        \n" +
                "                         ","");
        v.putExtra("contenido", contenido);
        v.putExtra("tipodoc", tipoDoc);
        startActivity(v);
    }

    //endregion

    //region LIVEDATE OBSERVERS LINEAS RETENIDAS Y PENDIENTES
    private void controlListasPendientes() {

        //TODO Hay que comprobar el tamaño de la lista de pendientes. Si se va a reenviar algo hay que mostrar un dialog
        GestorLineas.Companion.getLiveDataPendientes().observe(this, lista -> {

            if (GestorLineas.Companion.updateLineasPendientesFromBD(lista)) {
                //todo mostrar dialog de lineas pendientes
                new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog))

                        .setTitle("EXISTEN COMANDAS PENDIENTES DE ENVIAR")
                        .setMessage("¿Que desea hacer?")
                        .setPositiveButton("Enviar comandas a TPV", (dialog, which) -> GestorLineas.Companion.sendLineasPendientes(new ListenerCnx() {
                            @Override
                            public void notifySuccess(@NotNull CnxResponseK response) {
                                new ActualizarInfoMesas().execute(positionRango); //ACTUALIZAMOS LA GRID
                                Toast.makeText(SelectorMesa.this, "Lineas enviadas", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void notifyError(@NotNull CnxResponseK response) {
                                Toast.makeText(SelectorMesa.this, "No se ha podido enviar la comanda", Toast.LENGTH_LONG).show();
                            }
                        }))

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("Borrar comandas", (dialogInterface, i) -> GestorLineas.Companion.eliminarLineasPendientesBD())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        //LINEAS RETENIDAS COMPLETAMENTE EN SEGUNDO PLANO
        GestorLineas.Companion.getLiveDataRetenidas().observe(this, GestorLineas.Companion::updateLineasRetenidasFromBD);
    }

//endregion
}
