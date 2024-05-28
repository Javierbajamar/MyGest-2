package com.atecresa.activities;

/*
 * Clase inicial para login y configuraci�n
 */

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import com.atecresa.adaptadoresUIComanda.Ad_grid_operadores;
import com.atecresa.application.BuildConfig;
import com.atecresa.application.Global;
import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.cocina.Cocina_Activity;
import com.atecresa.notificaciones.Notifica;
import com.atecresa.preferencias.Constantes;
import com.atecresa.preferencias.PreferenciasActivity;
import com.atecresa.preferencias.PreferenciasManager;
import com.atecresa.preferencias.TpvConfig;

public class LoginV2 extends AppCompatActivity {


    private GridView gridOperadores;

    private Ad_grid_operadores adGridOP;

    private View layoutCarga;
    private View layoutOperadores; // PARA MOSTRAR EL GIF DE CARGA

    private androidx.appcompat.app.ActionBar actionBar;

    private MenuItem menuItem; // PARA EL GIF DE CARGA

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_content);
        Inicio.setContext(getApplicationContext());

        Inicio.setOnDebug(BuildConfig.DEBUG);
        //Nuevo cambio cargando opciones en memoria
        PreferenciasManager.cargarPreferencias();
        Inicio.inicializarGlobal(); //Por ahora lo dejamos con el SDK>22
        Inicio.setPantallaActual(this.getClass().getName());
        try {
            gridOperadores = findViewById(R.id.gridOperadores);
            adGridOP = new Ad_grid_operadores(LoginV2.this);
        } catch (Exception e1) {
            Log.e("ACTIVITY LOGIN", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e1.getMessage());
        }

        // LAYOUTS PRINCIPALES
        View layoutBase = findViewById(R.id.layout_base_login);
        this.layoutCarga = findViewById(R.id.layout_carga_operadores);
        this.layoutOperadores = findViewById(R.id.layout_grid_operadores);
        actionBar = getSupportActionBar();
        actionBar.setSubtitle(getString(R.string.subtitulo_action_operador).toUpperCase());
        actionBar.setDisplayShowHomeEnabled(false); //OCULTAMOS ICONO

        //TODO Test update. POR AHORA COMENTADO
        //UpdateHelper.checkUpdate(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        Inicio.setPantallaActual(this.getClass().getName());
        Inicio.setContext(LoginV2.this);
        updateConfig();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }


    private void configurarActionBar() {
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(TpvConfig.getAppBackColor());
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Inicio.gb.getIdTerminal().toUpperCase());

    }

    // RECEPTOR DE CAMBIOS EN LA WIFI

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                return true;
            case KeyEvent.KEYCODE_MENU:

                return false;
        }
        return false;
    }


    private final AdapterView.OnItemClickListener gridOPListener = (adapterView, v, position, id) -> {

        String nombreOperador;
        try {
            adapterView.setEnabled(false);

            nombreOperador = Inicio.gb.getOperadores().get(position).getString("DES");
            Inicio.gb.getOperadorActual().setNombre(nombreOperador);


            AlertDialog.Builder alert = new AlertDialog.Builder(LoginV2.this);

            alert.setTitle(getString(R.string.dialog_usuario_titulo));
            alert.setMessage(getString(R.string.dialog_usuario_mensaje));

            // Set an EditText view to get user input
            final EditText input = new EditText(LoginV2.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            alert.setView(input);

            alert.setPositiveButton(getString(R.string.dialog_ok),
                    (dialog, whichButton) -> {
                        String value = "";
                        if (input.length() > 0) {
                            value = input.getText().toString();
                        }
                        new Validar().execute(v.getId() + "", value);
                    });

            alert.setNegativeButton(getString(R.string.dialog_cancelar),
                    (dialog, whichButton) -> {
                        // Canceled.
                    });
            alert.show();
        } catch (Exception e) {
            Log.e("ACTIVITY LOGIN",
                    "Linea "
                            + Thread.currentThread().getStackTrace()[2]
                            .getLineNumber() + ": "
                            + e.getMessage());
        } finally {
            adapterView.setEnabled(true);
        }
    };

    /*
     * Menu principal(non-Javadoc)
     *
     * @MOSTRAMOS EL MENU PRINCIPAL
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login_v2, menu);
        menuItem = menu.getItem(0);
        return true;
    }

    /*
     * (non-Javadoc) LISTENER DEL MENU PRINCIPAL
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemConfig:
                Intent menuIntent = new Intent(LoginV2.this,
                        PreferenciasActivity.class);
                startActivity(menuIntent);
                return true;
            case R.id.action_recargar:
                menuItem = item;
                menuItem.setActionView(R.layout.item_barra_progreso);
                MenuItemCompat.expandActionView(menuItem);
                updateConfig();
                return true;
            case R.id.itemSalir:
                //TODO Ver si mostramos mensaje retenidas
                Inicio.gb = null;
                finish();
                return true;
            case R.id.itemMarket:
                try {
                    abrirMarket();
                    //Intent mesa=new Intent(LoginV2.this,MesasV2.class);
                    //startActivity(mesa);
                } catch (Exception ex) {
                    Notifica.mostrarToast(LoginV2.this,
                            getString(R.string.mensaje_error_market), 1,
                            Toast.LENGTH_SHORT);
                    Log.e("ACTIVITY LOGIN",
                            "Linea "
                                    + Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber() + ": "
                                    + ex.getMessage());
                }

                return true;
            case R.id.itemManual:
                //Intent manual = new Intent(LoginV2.this, ManualPDFActivity.class);
                //startActivity(manual);
                //PONDREMOS EL PDF EN LA NUBE
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://atecresa.com/manualespdf/mygest2.pdf")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.otras_opciones_login:
                Menu submenu = item.getSubMenu();
                submenu.getItem(1).setVisible(PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_CLOUD));
                return true;
            case R.id.itemUuid:
                mostrarAlertaEmpresa();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /*
     * MOSTRAR BARRA DE PROGRESO
     */

    private void mostrarBarraProgreso(final boolean show) {
        layoutCarga.setVisibility(show ? View.VISIBLE : View.GONE);
        layoutOperadores.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    /*
     * HILO QUE CARGA LOS OPERADORES
     */

    private class CargarOperadores extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            if (menuItem == null) {
                mostrarBarraProgreso(true);
            } else {
                mostrarBarraProgreso(false);
                menuItem.setActionView(R.layout.item_barra_progreso);
                MenuItemCompat.expandActionView(menuItem);
            }
            if (Inicio.gb != null && Inicio.gb.isOperadoresCargados()) {
                Inicio.gb.getListaOperadores().clear();
                adGridOP.notifyDataSetChanged();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (Inicio.gb != null) {
                    Inicio.gb.cargarJSONFunciones();
                    Inicio.gb.cargarConfig();
                    Inicio.gb.cargarOperadores();
                    Inicio.gb.ejecutarfuncion();
                    //ELIMINAMOS DE AQUÍ EL CONSULTAR LAS LINEAS PENDIENTES. LO HAREMOS CON UN OBSERVER EN MAPA MESAS
                }
            } catch (Exception e) {
                Log.e("ACTIVITY LOGIN",
                        "Linea "
                                + Thread.currentThread().getStackTrace()[2]
                                .getLineNumber() + ": "
                                + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (!LoginV2.this.isFinishing()) {

                //Evitar que el post execute trabaje con la activity finalizada al volver al hilo principal
                if (Inicio.gb != null) {
                    if (menuItem != null) {
                        MenuItemCompat.collapseActionView(menuItem);
                        MenuItemCompat.setActionView(menuItem, null);
                    }
                    mostrarBarraProgreso(false);

                    if (Inicio.gb.getError() == Constantes.error_conexion) {
                        Notifica.mostrarToast(LoginV2.this,
                                getString(R.string.mensaje_error_conexion), 2,
                                Toast.LENGTH_SHORT);
                    } else {
                        if (Inicio.gb.getError() == Constantes.error_licencia_demo) {
                            Notifica.mostrarToast(LoginV2.this,
                                    getString(R.string.mensaje_demo), 1,
                                    Toast.LENGTH_SHORT);
                        }

                        if (Inicio.gb.isOperadoresCargados()) {
                            gridOperadores.setNumColumns(Integer.parseInt(PreferenciasManager
                                    .getNumColumnas()));
                            try {
                                if (adGridOP == null)
                                    adGridOP = new Ad_grid_operadores(
                                            LoginV2.this);
                                gridOperadores.setAdapter(adGridOP);
                                gridOperadores
                                        .setOnItemClickListener(gridOPListener);

                                //layoutBase.setBackgroundColor(TpvConfig.getAppBackColor()); //DEJAMOS LAYOUT BASE SIN COLOR TPV
                            } catch (Exception e) {
                                Log.e("ACTIVITY LOGIN",
                                        "Linea "
                                                + Thread.currentThread()
                                                .getStackTrace()[2]
                                                .getLineNumber() + ": "
                                                + e.getMessage());
                            }

                            configurarActionBar();
                        } else {
                            Notifica.mostrarMensajeEstado(LoginV2.this);
                            if (adGridOP != null)
                                adGridOP.notifyDataSetChanged();
                        }
                    }
                }
            }


        }
    }

    /*
     * HILO QUE VALIDA
     */

    private class Validar extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {

            Notifica.mostrarProgreso(LoginV2.this,
                    getString(R.string.dialog_validando),
                    getString(R.string.dialog_espere) + "...", true);

        }

        @Override
        protected Void doInBackground(String... params) {
            Inicio.gb.validarUsuario(params[0], params[1]);

            if (PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_COMANDERO) || PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_CLOUD)) {
                Inicio.gb.cargarConfigMesas();
                Inicio.gb.descargarCarta();
                Inicio.gb.cargarGruposSeleccion();
                Inicio.gb.cargarVinculoSeleccion();
                Inicio.gb.cargarImpresoras(); // FUNCIÓN NUEVA PARA IMPRESORAS
                Inicio.gb.cargarPermisos();   // FUNCIÓN NUEVA PARA PERMISOS
                //TODO CONTROLAR SIGUIENTE CAMBIO
                if (PreferenciasManager.puedeSeleccionarCliente())
                    Inicio.gb.cargarClientes();   // FUNCIÓN NUEVA PARA CLIENTES
                if (PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_CLOUD))
                    Inicio.gb.cargarFormasDePago(); //NUEVA FUNCIÓN WEB PARA FORMAS DE PAGO
            } else {
                Inicio.gb.cargarConfigMesas();
                Inicio.gb.cargarMesasCocina();
            }
            Inicio.gb.ejecutarfuncion();
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {

            try {
                Notifica.mostrarProgreso(LoginV2.this, "", "", false);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (Inicio.gb.getError() == Constantes.error_conexion) {
                Notifica.mostrarToast(LoginV2.this,
                        getString(R.string.mensaje_error_conexion), 2,
                        Toast.LENGTH_LONG);
            } else {
                if ((Inicio.gb.getError() == Constantes.app_OK)
                        || (Inicio.gb.getError() == Constantes.error_licencia_demo)) {
                    if (Inicio.gb.getOperadorActual().isValidado()) {
                        if (PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_COMANDERO) || PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_CLOUD)) {
                            Intent IntentSelectorMesa = new Intent(
                                    LoginV2.this, SelectorMesa.class);
                            IntentSelectorMesa.putExtra("mesasOK", true);
                            startActivity(IntentSelectorMesa);
                        } else {
                            try {
                                Intent IntentCocina = new Intent(
                                        LoginV2.this, Cocina_Activity.class);
                                startActivity(IntentCocina);
                            } catch (Exception e) {
                                Log.e("ACTIVITY LOGIN",
                                        "Linea "
                                                + Thread.currentThread()
                                                .getStackTrace()[2]
                                                .getLineNumber() + ": "
                                                + e.getMessage());
                            }
                        }

                        try {
                            mostrarBarraProgreso(false);
                        } catch (Exception e) {
                            Log.e("ACTIVITY LOGIN",
                                    "Linea "
                                            + Thread.currentThread()
                                            .getStackTrace()[2]
                                            .getLineNumber() + ": "
                                            + e.getMessage());
                        }
                    } else {
                        try {
                            Notifica.mostrarToast(LoginV2.this,
                                    getString(R.string.mensaje_error_password), 2,
                                    Toast.LENGTH_SHORT);
                            mostrarBarraProgreso(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }

    private void updateConfig() {

        if (PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_CLOUD) && (PreferenciasManager.getUuid().equals(""))) {
            //Mostrar mensaje para luego abrir el scan de QR
            mostrarAlertaEmpresa();
        } else if (!PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_CLOUD)
                && PreferenciasManager.getIp().equals("")) {
            Notifica.mostrarToast(LoginV2.this,
                    getString(R.string.mensaje_conf_servidor), 1,
                    Toast.LENGTH_SHORT);
            Intent preferenciasIntent = new Intent(LoginV2.this,
                    PreferenciasActivity.class);
            startActivity(preferenciasIntent);

        } else {
            if (Inicio.gb == null) {
                Inicio.gb = new Global(Inicio.getNserie());
            }
            try {
                new CargarOperadores().execute();
            } catch (Exception e) {
                Log.e("ACTIVITY LOGIN",
                        "Linea "
                                + Thread.currentThread().getStackTrace()[2]
                                .getLineNumber() + ": "
                                + e.getMessage());
            }

        }
    }

    //region GOOGLE PLAY
    private void abrirMarket() {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.atecresa.Mygest2"));
            startActivity(browserIntent);
        } catch (Exception ex) {
            Toast.makeText(LoginV2.this, "No se ha podido conectar con Google Play", Toast.LENGTH_LONG).show();
        }

    }
    //endregion

    //region ESCANEAR QR

    private void mostrarAlertaEmpresa() {

        android.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.app.AlertDialog.Builder(LoginV2.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new android.app.AlertDialog.Builder(LoginV2.this);
        }
        builder.setTitle("CONFIGURACIÓN DE EMPRESA")
                .setMessage("¿Quiere escanear ahora el código QR de la empresa?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> scan())
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    // do nothing
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void scan() {
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.setPackage("com.google.zxing.client.android");
            startActivityForResult(intent, Constantes.scan_empresa);
        } catch (Exception e) {

            final android.app.AlertDialog d = new android.app.AlertDialog.Builder(LoginV2.this)
                    .setPositiveButton(R.string.dialog_si,
                            (dialog, id) -> {
                                Intent i = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://market.android.com/details?id=com.google.zxing.client.android&hl=es"));
                                startActivityForResult(i, Constantes.scan_empresa);
                            }).setNegativeButton(R.string.dialog_cancelar, null)
                    .setMessage(R.string.msg_sin_empresa)
                    .create();
            d.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {

            if (requestCode == Constantes.scan_empresa) {
                PreferenciasManager.setUuid(intent.getStringExtra("SCAN_RESULT"));
                Inicio.inicializarGlobal();
                updateConfig();
            }
        }
    }

    //endregion

    @Override
    protected void onRestart() {
        //POR SI VOLVEMOS DESDE PREFERENCIAS O DESDE OTRA ACTIVITY
        PreferenciasManager.cargarPreferencias();
        super.onRestart();
    }


}
