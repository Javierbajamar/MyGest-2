package com.atecresa.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.atecresa.adaptadoresUIComanda.Ad_combo_impresoras;
import com.atecresa.adaptadoresUIComanda.Ad_combo_unidades;
import com.atecresa.adaptadoresUIComanda.Ad_grid_articulos;
import com.atecresa.adaptadoresUIComanda.Ad_grid_familias;
import com.atecresa.adaptadoresUIComanda.Ad_linea_comanda;
import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.clientes.Clientes;
import com.atecresa.clientes.ClientesActivity;
import com.atecresa.gestionLineasComanda.GestorLineas;
import com.atecresa.notificaciones.Notifica;
import com.atecresa.preferencias.Constantes;
import com.atecresa.preferencias.PreferenciasManager;
import com.atecresa.preferencias.TpvConfig;
import com.atecresa.print.DownloadTicket;
import com.atecresa.print.Print_Bluetooth;
import com.atecresa.util.Formateador;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class ComandaActivityV3 extends BaseActivity {

    private ListView listaComanda;
    private GridView gridArticulos;

    private Ad_linea_comanda adComanda;


    private LinearLayout layoutGrids; // CONTENEDOR DE LAS GRIDS

    private View layoutGridFamilias;
    private View layoutGridArticulos;

    private TextView txtInfo;
    private MenuItem menuItem; // PARA EL GIF DE CARGA

    private Button btVolver;
    private Button btUnidades;

    private String comensales = "";

    private View capaSubirLineas;

    private int indiceLinea = 0;

    private static final int REQUEST_TEXT = 0;

    private EjecutarFuncion ec;

    private boolean moviendoLineas = false;

    private AlertDialog alert;
    private double unidadesInsertar = 1;

    /*
     * BOOLEANO PARA CONTROL DE QUE NO TOQUEMOS VARIAS VECES EL PEDIR COMANDA
     */

    private boolean enviandoComanda = false;

    //TOOLBAR Y BOTTOMAPPBAR
    Toolbar toolbar;
    BottomAppBar bottomAppBar;
    FloatingActionButton fab;
    ColorDrawable colorDrawableFondo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comanda_activity); //R.layout.comanda_activity_content_v2

        controlarBloqueo();
        Inicio.setPantallaActual(this.getClass().getName());
        //COLORFONDO
        colorDrawableFondo = new ColorDrawable();
        colorDrawableFondo.setColor(TpvConfig.getAppBackColor());
        adaptarUI();


        try {
            // INICIALIZAMOS OBJETOS PARA GESTIONAR GRIDS
            this.layoutGridFamilias = findViewById(R.id.layout_grid_familias);
            this.layoutGridArticulos = findViewById(R.id.layout_grid_articulos);

            Ad_grid_familias adGridFamilias = new Ad_grid_familias(ComandaActivityV3.this);
            GridView gridFamilias = findViewById(R.id.gridFamilias);

            gridFamilias.setAdapter(adGridFamilias);
            gridFamilias.setOnItemClickListener(listenerGridFamilias);

            gridArticulos = findViewById(R.id.gridArticulos);

            gridFamilias.setNumColumns(Integer.parseInt(PreferenciasManager.getNumColumnas()));
            gridArticulos.setNumColumns(Integer.parseInt(PreferenciasManager.getNumColumnas()));

            /*
             * LISTA PARA LA COMANDA
             */
            listaComanda = findViewById(R.id.listComanda);
            listaComanda.setStackFromBottom(false);

            adComanda = new Ad_linea_comanda(ComandaActivityV3.this);
            listaComanda.setAdapter(adComanda);
            listaComanda.setOnItemClickListener(listenerListaComanda);
            listaComanda.setOnItemLongClickListener(listenerlongClickComanda);

            this.btVolver = findViewById(R.id.bt_volver);
            this.btVolver.setTextColor(TpvConfig.getAppForecolor());
            this.btVolver.setBackgroundColor(TpvConfig.getAppBackColor());
            this.btVolver.setTextSize(PreferenciasManager.getTamFuenteNew());

            this.btVolver.setOnClickListener(arg0 -> mostrarCapaArticulos(false));

            this.btUnidades = findViewById(R.id.bt_unidades);
            this.btUnidades.setTextColor(TpvConfig.getAppForecolor());
            this.btUnidades.setBackgroundColor(TpvConfig.getAppBackColor());
            this.btUnidades.setTextSize(PreferenciasManager.getTamFuenteNew());

            this.btUnidades.setOnClickListener(arg0 -> mostrarGridUnidades());

            this.capaSubirLineas = findViewById(R.id.layout_orden_lineas);

            ImageView imgBtBajar = findViewById(R.id.imgBtBajar);
            ImageView imgBtSubir = findViewById(R.id.imgBtSubir);

            imgBtSubir.setOnClickListener(arg0 -> {
                try {
                    JSONObject linea = Inicio.gb.getLineasVisiblesComanda().get(
                            indiceLinea);
                    if (Inicio.gb.NmoverLinea(indiceLinea, -1)) {
                        indiceLinea = Inicio.gb.getLineasVisiblesComanda().indexOf(
                                linea);
                        actualizarColeccionComanda();
                        listaComanda.setSelection(indiceLinea);
                    } else {
                        Notifica.mostrarToast(ComandaActivityV3.this,
                                getString(R.string.mensaje_linea_mover_facturada),
                                2, Toast.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

            imgBtBajar.setOnClickListener(arg0 -> {
                try {
                    JSONObject linea = Inicio.gb.getLineasVisiblesComanda().get(
                            indiceLinea);
                    if (Inicio.gb.NmoverLinea(indiceLinea, 1)) {
                        indiceLinea = Inicio.gb.getLineasVisiblesComanda().indexOf(
                                linea);
                        actualizarColeccionComanda();
                        listaComanda.setSelection(indiceLinea);
                    } else {
                        Notifica.mostrarToast(ComandaActivityV3.this,
                                getString(R.string.mensaje_linea_mover_facturada),
                                2, Toast.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // CAPA PARA MODIFICAR LINEAS, SUBIRLAS O BAJARLAS
            ImageView imgBtCerrar = findViewById(R.id.imgBtCerrar);

            imgBtCerrar.setOnClickListener(arg0 -> {
                try {
                    moviendoLineas = false;
                    capaSubirLineas.setVisibility(View.GONE);
                    layoutGrids.setVisibility(View.VISIBLE);
                    Inicio.gb.quitarSeleccion();
                    actualizarColeccionComanda();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            enviandoComanda = false;

            toolbar = findViewById(R.id.toolbarComanda);

            bottomAppBar = findViewById(R.id.bottom_app_bar);
            bottomAppBar.replaceMenu(R.menu.menu_comanda_buttonbar);
            bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
            fab = findViewById(R.id.fab);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //COLORES
                fab.setBackgroundTintList(ColorStateList.valueOf(colorDrawableFondo.getColor()));
                bottomAppBar.setBackgroundTintList(ColorStateList.valueOf(colorDrawableFondo.getColor()));
            }
            fab.setOnClickListener(view -> {
                //ENVIAR COMANDA
                enviarComanda();
            });
            //CONFIGURAMOS MENÚ
            selectMenuActionbar();

        } catch (Exception e) {
            finish();
        }

    }

    //FUNCIÓN PARA USAR UN MENÚ SUPERIOR O INFERIOR
    private void selectMenuActionbar() {
        AppBarLayout barraSuperior = findViewById(R.id.barra_superior_comanda);
        View layout_info=findViewById(R.id.layout_info);
        layout_info.setBackgroundColor(TpvConfig.getAppBackColor());
        if (PreferenciasManager.isModoUnamano()) {
            setSupportActionBar(bottomAppBar);
            barraSuperior.setVisibility(View.GONE);
            bottomAppBar.setVisibility(View.VISIBLE);
            fab.show();
        } else {
            toolbar.setTitle(Inicio.gb.getTextoRango() + " " + Inicio.gb.getMesa()); //VIGILAR CAMBIO
            toolbar.setBackgroundColor(TpvConfig.getAppBackColor());
            setSupportActionBar(toolbar);
            bottomAppBar.setVisibility(View.GONE);
            fab.hide();
            barraSuperior.setVisibility(View.VISIBLE);
            Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
    }

    private void showInfo() {
        comensales = "";
        // Cambio por get de la clase mesa
        try {
            if ((Inicio.gb.getComensales() != null)
                    && (!Inicio.gb.getMesaActual().getComensales().equals(""))
                    && (!Inicio.gb.getMesaActual().getComensales().equals("0"))) {
                comensales = " " + getString(R.string.txt_comensales) + ": "
                        + Inicio.gb.getMesaActual().getComensales()+" | ";
            }

            txtInfo = findViewById(R.id.txtInfoComanda);

            txtInfo.setTextSize(PreferenciasManager.getTamFuenteNew());
            String info;
            if (PreferenciasManager.isModoUnamano()){
                info=Inicio.gb.getTextoRango() + " " + Inicio.gb.getMesa()+" | ";
            }else
                info="";
            if (Inicio.gb.hayLineasNuevas()) {
                info += comensales + getString(R.string.txt_total) + " : ?";
            } else {
                info += comensales + getString(R.string.txt_total) + " : "
                        + Inicio.gb.getMesaActual().getTotal() + " €";
            }
            txtInfo.setText(info.toUpperCase());
            txtInfo.setTextColor(Color.WHITE);


            Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(false); //OCULTAMOS ICONO
            if (Inicio.gb.isMesaBloqueada()) {  //Inicio.gb.getMesaActual().isBloqueada()
                toolbar.setSubtitle(getString(R.string.mensaje_mesa_bloqueada).toUpperCase());
            } else {
                toolbar.setSubtitle(Inicio.gb.getMesaActual().getCliente().getNombre());
            }


            toolbar.setBackgroundDrawable(colorDrawableFondo);
        } catch (Exception e) {
            Log.e("ACTIVITY COMANDA", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Inicio.setContext(ComandaActivityV3.this);
        Inicio.setPantallaActual(this.getClass().getName());
        showInfo();
    }


    @Override
    protected void onDestroy() {
        Formateador.reiniciarContadorLineas();
        new EjecutarFuncion().execute("desbloquearmesa");
        super.onDestroy();
    }

    /*
     * recogemos resultado del buscador y del formulario de vínculos
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //AGREGADOS TRY CATCH PARA CONTROL DE java.lang.IllegalStateException

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constantes.REQUEST_BUSCAR_ARTICULOS:
                    unidadesInsertar = 1;
                    listaComanda.setStackFromBottom(true);
                    if (!PreferenciasManager.isModoUnamano()) {
                        menuItem.setIcon(R.drawable.ic_action_confirmar_comanda);
                        menuItem.setTitle(getString(R.string.menu_comanda_pedir));
                        menuItem.setVisible(true);
                    }

                    this.adComanda.notifyDataSetChanged();
                    break;
                case Constantes.REQUEST_COMANDO_VOZ:
                    if (data != null) {
                        ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        ejecutarComandoVoz(result.get(0), "");
                    }
                    break;
                case Constantes.REQUEST_COBRAR_MESA:
                    //Si recibimos el booleano de la mesa cobrada, directamente hacemos un finish. El TPV finaliza la mesa por nosotros


                    if (data.getBooleanExtra("finalizar", false))
                        new FinalizarDoc().execute();

                    break;
                case Constantes.REQUEST_BUSCAR_CLIENTES:
                    Objects.requireNonNull(getSupportActionBar()).setSubtitle(Inicio.gb.getMesaActual().getCliente().getNombre());
                    if (Inicio.gb.hayLineasRecibidas() && Inicio.gb.getMesaActual().isClienteModificado()) {
                        ec = new EjecutarFuncion();
                        ec.execute("enviarCliente");
                    }
                    break;
                case Constantes.REQUEST_VINCULOS:
                    unidadesInsertar = 1;
                    listaComanda.setStackFromBottom(true);
                    if (!PreferenciasManager.isModoUnamano()) {
                        menuItem.setIcon(R.drawable.ic_action_confirmar_comanda);
                        menuItem.setTitle(getString(R.string.menu_comanda_pedir));
                        menuItem.setVisible(true);
                    }
                    this.adComanda.notifyDataSetChanged();
                    break;
            }
        }

    }


    // COMPROBAMOS ORIENTACIÓN Y REPARTIMOS EL ESPACIO
    // HORIZONTAL 1/1
    // VERTICAL 1/2
    private void adaptarUI() {

        int pantalla = getResources().getConfiguration().orientation;
        // LAYOUT PRINCIPAL DE EL FORM
        LinearLayout layoutBase = findViewById(R.id.layout_principal);
        // CONTENEDOR DE COMANDA
        LinearLayout layoutComanda = findViewById(R.id.layout_principal_comanda);
        layoutGrids = findViewById(R.id.layout_grids);
        //layoutGrids.setBackgroundColor(TpvConfig.getAppBackColor()); //LO DEJAMOS EN GRIS

        // layoutBase.setBackgroundColor(Inicio.gb.getAppBackColor());

        if (pantalla == 1) { // VERTICAL
            layoutBase.setOrientation(LinearLayout.VERTICAL);
            layoutComanda.setLayoutParams(new LinearLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT, 0, 4));
            layoutGrids.setLayoutParams(new LinearLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT, 0, 6));
        } else {
            layoutBase.setOrientation(LinearLayout.HORIZONTAL);
            layoutComanda.setLayoutParams(new LinearLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            layoutGrids.setLayoutParams(new LinearLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        }

        if (Inicio.gb.getMesaActual() != null) {
            if (Inicio.gb.getMesaActual().isBloqueada() ||
                    Objects.requireNonNull(Inicio.gb.getMesaActual()).isPagando()) { //HAREMOS SEGUIMIENTO DE LA CONDICIÓN !=NULL. POSIBLE BUG
                layoutGrids.setVisibility(View.GONE);
                if (!Inicio.gb.hayLineasRecibidas()) {
                    layoutBase.setBackgroundColor(TpvConfig.getAppBackColor());
                }
            }

        } else {
            layoutGrids.setVisibility(View.VISIBLE);
        }

    }

    private void modificarIconoSend() {
        if (Inicio.gb != null) {
            String info;
            if (PreferenciasManager.isModoUnamano()){
                info=Inicio.gb.getTextoRango() + " " + Inicio.gb.getMesa()+" | ";
            }else
                info="";
            info += comensales + getString(R.string.txt_total) + " : ?";
            txtInfo.setText(info.toUpperCase());
            if (!PreferenciasManager.isModoUnamano()) {
                if (Inicio.gb.hayLineasNuevas()
                        || Inicio.gb.hayLineasParaEliminar()
                        || Inicio.gb.getMesaActual().isClienteModificado()
                        && Inicio.gb.hayLineasRecibidas()) {
                    menuItem.setVisible(true);
                } else {
                    menuItem.setVisible(false);
                }
            }

        }
    }

    /*
     * AQUÍ VAMOS A HACER UNA FUNCIÓN DE ANIMACIÓN IGUAL QUE EN LOGIN PERO
     * ALTERNANDO ENTRE LA GRID DE ARTÍCULOS Y LA DE FAMILIAS YEAH!
     */
    private void mostrarCapaArticulos(final boolean show) {
        // Sin animación
        unidadesInsertar = 1;
        if (layoutGridArticulos!=null&&layoutGridFamilias!=null){
            layoutGridArticulos.setVisibility(show ? View.VISIBLE : View.GONE);
            layoutGridFamilias.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if (PreferenciasManager.isModoUnamano()) {
            //BOTTOMBAR
            getMenuInflater().inflate(R.menu.menu_comanda_buttonbar, menu);
            menuItem = menu.getItem(0);
        } else {
            //ACTIONBAR
            getMenuInflater().inflate(R.menu.menu_comanda_actionbar, menu);
            menuItem = menu.getItem(0);
            if (Inicio.gb.isMesaBloqueada())
                menu.getItem(0).setEnabled(false);
            else
                menu.getItem(0).setEnabled(true);
            if (Inicio.gb.hayLineasNuevas()) {
                menuItem.setIcon(R.drawable.ic_check_v2);
                menuItem.setVisible(true);
            } else {
                menuItem.setVisible(false);  //no mostrar este icono a menos que haya algo que enviar
            }
        }


        //POR AHORA PARA LA DEMO, VISIBLE SI ESTÁ LA PLATAFORMA DE PAGO CONFIGURADA

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_comanda_otras_opciones:

                Menu submenu = item.getSubMenu();
                try {
                    submenu.getItem(0).setVisible(true) ;//LO DEHAMOS FIJO QUE SE VEA PARA PROBAR
                            //&&Inicio.gb.getOperadorActual().isPuedeCobrar()); //TODO Ojo, que vamos a funcionar igual que la nube
                    submenu.getItem(1).setVisible(!Clientes.ITEMS.isEmpty()); // CLIENTES
                    submenu.getItem(2).setVisible(Inicio.gb.hayLineasRecibidas()); // PEDIR CUENTA
                    submenu.getItem(3).setVisible(Inicio.gb.hayLineasNuevas()); // RETENER LINEAS
                } catch (Exception e) {
                    Log.e("ACTIVITY COMANDA", "Linea "
                            + Thread.currentThread().getStackTrace()[2].getLineNumber()
                            + ": " + e.getMessage());
                }
                return true;
            case android.R.id.home:
            case R.id.action_comanda_cancela:
                //activarVoz();
                //TODO PRUEBA BOTTOMBAR
                cancelarPedido();
                return true;
            case R.id.action_comanda_pedir:
                enviarComanda();
                return true;
            case R.id.action_comanda_buscar_articulo:
                Intent i = new Intent(ComandaActivityV3.this, Buscador.class);
                i.putExtra("modo_voz", false);
                ComandaActivityV3.this.startActivityForResult(i, Constantes.REQUEST_BUSCAR_ARTICULOS);

                return true;
            case R.id.action_comanda_retener:
                if (GestorLineas.Companion.hayLineasNuevas()) {
                    GestorLineas.Companion.guardarLineasRetenidasBD(Inicio.gb.getMesaActual().getNumero());
                    Notifica.mostrarToast(ComandaActivityV3.this,
                            getString(R.string.mensaje_mesa_retenida), 1,
                            Toast.LENGTH_SHORT);
                    ec = new EjecutarFuncion();
                    ec.execute("desbloquearmesa");
                    finish();
                } else {
                    Notifica.mostrarToast(this,
                            getString(R.string.mensaje_comandas_sin_lineas), 1,
                            Toast.LENGTH_SHORT);
                }

                return true;
            case R.id.action_comanda_cambio_mesa:

                Intent mesasIntent = new Intent(ComandaActivityV3.this,
                        SelectorMesasLibres.class);
                startActivity(mesasIntent);
                return true;
            case R.id.action_comanda_pedir_cuenta:
                pedirCuenta();
                return true;
            case R.id.action_comanda_comensales:
                mostrarDialogoComensales(false);
                return true;
            case R.id.action_comanda_cambiar_cliente:
                Intent buscador_clientes = new Intent(ComandaActivityV3.this,
                        ClientesActivity.class);
                ComandaActivityV3.this.startActivityForResult(buscador_clientes, Constantes.REQUEST_BUSCAR_CLIENTES);
                return true;
            case R.id.action_comanda_cobrar:
                pedirImporte();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //region FUNCIONES COMANDA
    private void enviarComanda() {
        if (!enviandoComanda) {
            if (Inicio.gb.hayLineasNuevas() || Inicio.gb.hayLineasParaEliminar()) {
                if ((Inicio.gb.isPedirComensales()) && (Integer.parseInt(Inicio.gb.getMesaActual().getComensales()) == 0)) {
                    mostrarDialogoComensales(true);
                } else {
                    EnviarComanda ec = new EnviarComanda();
                    ec.execute();
                }
            } else {
                ec = new EjecutarFuncion();
                ec.execute("desbloquearmesa"); // SÓLO DESBLOQUEA
                finish();
            }
        }
    }

    private void cambiarMesa() {
        Intent mesasIntent = new Intent(ComandaActivityV3.this,
                SelectorMesasLibres.class);
        startActivity(mesasIntent);
    }

    /*
    HAY QUE CONTROLAR QUE TENGAMOS COBROS YA O LA POSIBILIDAD DE COBRAR DESDE MYGETS CONECTADO A TPV
     */
    private void cobrarMesa() {
        //TODO PARA PROBAR EL DIALOG DE LA FIRMA

        if (Inicio.gb.getLineasVisiblesComanda().size() > 0) {
            if (!Inicio.gb.hayLineasNuevas()) {
                if (TpvConfig.hayImpresoras()
                        && PreferenciasManager.seleccionarImpresoraPrefactura()) {
                    mostrarSelectorImpresora();
                } else {
                    if (PreferenciasManager.isPrefacturaBluetooth() && !PreferenciasManager.getImpresoraBluetooth().equals("")) {
                        new DescargarTxtTicket().execute(); //ESTO SE SUPONE QUE YA PONE LA MESA EN PREFACTURA
                    } else if (Inicio.gb.getMesaActual().getTotalCobrado()!=null) {
                        ec = new EjecutarFuncion();
                        ec.execute("cobrarCuenta", ";1;" + Inicio.gb.getMesaActual().getTotalCobrado());
                    }
                }
            } else {
                Notifica.mostrarToast(
                        ComandaActivityV3.this,
                        getString(R.string.mensaje_no_se_puede_pedir_cuenta),
                        2, Toast.LENGTH_SHORT);
            }
        } else {
            Notifica.mostrarToast(ComandaActivityV3.this,
                    getString(R.string.mensaje_comandas_sin_lineas), 1,
                    Toast.LENGTH_SHORT);
        }

        //startActivityForResult(new Intent(ComandaActivityV3.this, ActivityCobrosTPV.class), Constantes.REQUEST_COBRAR_MESA);
        /*
        if (Inicio.gb.hayLineasNuevas()) {
            Toast.makeText(ComandaActivityV3.this, "No es posible cobrar esta mesa, hay nuevos platos sin enviar", Toast.LENGTH_LONG).show();
        }  else if (!Inicio.gb.hayLineasNuevas() && Inicio.gb.hayLineasRecibidas()) {
            startActivityForResult(new Intent(ComandaActivityV3.this, CobroCloudActivity.class), Constantes.REQUEST_COBRAR_MESA);
        } else if (Inicio.gb.getLineasVisiblesComanda().size() == 0) {
            Toast.makeText(ComandaActivityV3.this, "Mesa sin platos", Toast.LENGTH_LONG).show();
        }
        */

    }

    private void pedirImporte(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(ComandaActivityV3.this);
        builder.setTitle("Factura Mesa " + Inicio.gb.getMesaActual().getNumero());
        final EditText input = new EditText(ComandaActivityV3.this);
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        input.setText(Formateador.formatearImporteString(Inicio.gb.getMesaActual().getTotal()));
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        input.setFocusable(true);
        input.selectAll();

        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                input.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager= (InputMethodManager) ComandaActivityV3.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        input.requestFocus();

        builder.setMessage(getString(R.string.cobro_dialog_Importe));
        builder.setView(input);
        builder.setIcon(R.drawable.ic_euro_symbol);

        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Inicio.gb.getMesaActual().setTotalCobrado(input.getText().toString());
                        cobrarMesa();
                        return;
                    }
                });

        builder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Inicio.gb.getMesaActual().setTotalCobrado(null);
                        dialog.cancel();
                        return;
                    }
                });

        builder.create();
        builder.show();
    }

    private boolean comensalesModificados = false;

    private void mostrarDialogoComensales(final boolean hacerPedido) {
        final Dialog dialog = new Dialog(ComandaActivityV3.this);
        dialog.setContentView(R.layout.comanda_dialog_editor_comensales);
        final EditText txtUnid = dialog
                .findViewById(R.id.txt_unid_comensales);
        dialog.setTitle(Inicio.gb.getTextoRango() + " " + Inicio.gb.getMesa());

        String comensales = Inicio.gb.getMesaActual().getComensales();

        if (comensales.equals("0"))
            comensales = "2";

        txtUnid.setText(comensales);
        txtUnid.setSelection(txtUnid.getText().length());
        txtUnid.requestFocus();

        Button btDialogSumar = dialog
                .findViewById(R.id.bt_dialog_sumar_comensales);
        // if button is clicked, close the custom dialog
        btDialogSumar.setOnClickListener(v -> {
            int unid = Integer.parseInt(txtUnid.getText().toString()) + 1;
            txtUnid.setText(unid + "");
        });

        Button btDialogRestar = dialog
                .findViewById(R.id.bt_dialog_restar_comensales);
        // if button is clicked, close the custom dialog
        btDialogRestar.setOnClickListener(v -> {
            int unid = Integer.parseInt(txtUnid.getText().toString()) - 1;
            if (unid == 0) {
                unid = 1;
            }
            txtUnid.setText(unid + "");
        });

        Button btCerrarDialogComensales = dialog
                .findViewById(R.id.bt_cerrar_dialog_comensales);
        btCerrarDialogComensales.setOnClickListener(v -> dialog.dismiss());

        Button btDialogGuardar = dialog
                .findViewById(R.id.bt_dialog_guardar_comensales);
        // if button is clicked, close the custom dialog
        btDialogGuardar.setOnClickListener(v -> {

            Inicio.gb.setComensales(txtUnid.getText().toString());
            Inicio.gb.getMesaActual().setComensales(
                    txtUnid.getText().toString());
            String info = getString(R.string.txt_comensales) + ": " + Inicio.gb.getComensales() + " | " + getString(R.string.txt_total) + " :" + Inicio.gb.getMesaActual().getTotal() + " €";
            txtInfo.setText(info.toUpperCase());
            // SI TENGO UNA COMANDA YA EN PANTALLA Y NO HAY LINEAS
            // NUEVAS,ENVIO UN SETCOMENSALES
            comensalesModificados = true;
            if (hacerPedido) {
                EnviarComanda ec = new EnviarComanda();
                ec.execute();

            } else if (Inicio.gb.hayLineasRecibidas()) {
                EjecutarFuncion ec = new EjecutarFuncion();
                ec.execute("setComensales");
            }
            dialog.dismiss();

        });

        dialog.show();
    }

    /*
     * FUNCIÓN PARA CANCELAR EL PEDIDO MOSTRAMOS DIALOGO
     */
    private void cancelarPedido() {

        if ((Inicio.gb.hayLineasNuevas())) {
            AlertDialog.Builder alert = new AlertDialog.Builder(
                    ComandaActivityV3.this);

            alert.setTitle(getString(R.string.dialog_pedido_en_curso));
            alert.setMessage(getString(R.string.dialog_cancelar_pedido_en_curso));

            alert.setPositiveButton(getString(R.string.dialog_si),
                    (dialog, whichButton) -> {
                        Inicio.gb.getLineasVisiblesComanda().clear();
                        Inicio.gb.eliminarListaLineasEliminadas();
                        /*ESTO LO HACEMOS EN EL ONDESTROY
                        ec = new EjecutarFuncion();
                        ec.execute("desbloquearmesa");
                        */
                        finish();
                    });

            alert.setNegativeButton(getString(R.string.dialog_no),
                    (dialog, whichButton) -> {
                        // Canceled.
                    });

            alert.show();
        } else {
            ec = new EjecutarFuncion();
            ec.execute("desbloquearmesa");
            finish();
        }

    }

    //endregion

    //region LISTENERS ONCLICK

    private final AdapterView.OnItemClickListener listenerGridFamilias = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View v,
                                int position, long id) {

            if (Inicio.gb.isMesaBloqueada()) {
                Toast.makeText(ComandaActivityV3.this, getString(R.string.mensaje_mesa_bloqueada), Toast.LENGTH_LONG).show();
            } else {
                try {
                    activarControl(adapterView, false);
                    Inicio.gb.cargarArticulos(position);
                    Ad_grid_articulos adGridArticulos = new Ad_grid_articulos(ComandaActivityV3.this);
                    gridArticulos.setAdapter(adGridArticulos);
                    gridArticulos.setOnItemClickListener(listenerGridArticulos);
                    mostrarCapaArticulos(true);
                } catch (Exception ex) {
                    Log.e("ACTIVITY COMANDA",
                            "Linea "
                                    + Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber() + ": "
                                    + ex.getMessage());
                } finally {
                    activarControl(adapterView, true);
                }
            }


        }
    };

    private final AdapterView.OnItemClickListener listenerGridArticulos = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View v,
                                int position, long id) {
            double unidadesVinculos;

            try {
                JSONObject articulo = Inicio.gb.getArticulos().get(position);
                activarControl(adapterView, false);
                if (unidadesInsertar < 1)
                    unidadesVinculos = 1;
                else
                    unidadesVinculos = unidadesInsertar;
                Inicio.gb.getVinculos().setUnidadesMaestro(unidadesVinculos);
                if (Inicio.gb.getVinculos().hayVinculos()
                        && Inicio.gb.getVinculos().apilarVinculos(articulo)) {

                    Intent vi = new Intent(ComandaActivityV3.this, Vinculos.class);
                    vi.putExtra("position", position);
                    vi.putExtra("isBusqueda", false);
                    vi.putExtra("unidadesInsertar", unidadesInsertar);
                    ComandaActivityV3.this.startActivityForResult(vi, REQUEST_TEXT);
                } else {
                    Inicio.gb.agregarNuevaLinea(position, unidadesInsertar,
                            Constantes.lista_normal);
                    unidadesInsertar = 1;
                    if (!PreferenciasManager.tecladoFijo()) {
                        mostrarCapaArticulos(false);
                    }
                }

                modificarIconoSend();
                actualizarColeccionComanda();// LINEA ORIGINAL, VOLVER A
                // PONERLA LUEGO
                // posicionarnos al final de la lista
                listaComanda.setStackFromBottom(true);
            } catch (Exception e) {
                Log.e("ACTIVITY COMANDA", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
            } finally {
                activarControl(adapterView, true);
            }
        }
    };

    //endregion

    //region LISTENER COMANDA

    private EditText tvUnid;

    private final AdapterView.OnItemClickListener listenerListaComanda = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View v,
                                final int position, long id) {

            if ((!moviendoLineas) && !Inicio.gb.isMesaBloqueada()) {
                JSONObject linea = Inicio.gb.getLineasVisiblesComanda().get(
                        position);

                String descripcion = "";
                String unidades = "";
                String observacion = "";
                int tipo = 0;
                String tipo_articulo = "";
                String precio = "";
                double unidadesDouble = 0.0;
                try {
                    descripcion = linea.getString("DESCRIPCION");
                    unidades = Formateador.formatearUds(linea.getDouble("UNID"));
                    unidadesDouble = linea.getDouble("UNID");
                    observacion = linea.getString("OBSERVACION");
                    tipo = linea.getInt("TIPO");
                    tipo_articulo = linea.getString("T");
                    precio = linea.getString("PRECIO");
                } catch (JSONException e) {
                    Log.e("ACTIVITY COMANDA",
                            "Linea "
                                    + Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber() + ": "
                                    + e.getMessage());
                }

                if ((!Inicio.gb.getEstadoMesa().equals("PREFACTURA")) // &&
                        // (tipo
                        // !=
                        // Constantes.linea_nueva_menu_maestro)
                        && (tipo != Constantes.linea_nueva_menu_detalle)
                        && (tipo != Constantes.linea_recibida_menu_detalle)
                        && (tipo != Constantes.linea_recibida_menu_maestro)
                        && unidadesDouble > 0) {


                    // HE PUESTO UNA CONDICIÓN QUE NO SE DA SIMPLEMENTE PARA MANTENER LA FUNCIÓN COMPLETA
                    // PARA VOLVER A COMO ERA ANTES SOLO TENEMOS QUE PONER equals("H")
                    if (!tipo_articulo.equals("W")) {
                        final Dialog dialog = new Dialog(ComandaActivityV3.this);

                        dialog.setContentView(R.layout.comanda_dialog_editor_linea);

                        final EditText txtObservaciones = dialog
                                .findViewById(R.id.tv_edit);

                        tvUnid = dialog
                                .findViewById(R.id.tv_celda_unidades);


                        tvUnid.setOnClickListener(v17 -> tvUnid.setText(""));

                        final EditText txtPrecio = dialog
                                .findViewById(R.id.tv_celda_precio);

                        Button btDialogSumar = dialog
                                .findViewById(R.id.bt_dialog_sumar);

                        btDialogSumar.setOnClickListener(v16 -> {
                            if (!tvUnid.getText().toString().equals("")) {
                                double unid = Double.parseDouble(tvUnid
                                        .getText().toString()
                                        .replace(",", "."));
                                unid = Formateador.sumarUnidades(unid);
                                tvUnid.setText(Formateador.formatearUds(unid));
                            }
                        });

                        Button btDialogRestar = dialog
                                .findViewById(R.id.bt_dialog_restar);

                        btDialogRestar
                                .setOnClickListener(v15 -> {
                                    if (!tvUnid.getText().toString()
                                            .equals("")) {
                                        double unid = Double
                                                .parseDouble(tvUnid
                                                        .getText()
                                                        .toString()
                                                        .replace(",", "."));
                                        unid = Formateador.restarUnidades(unid);
                                        tvUnid.setText(Formateador
                                                .formatearUds(unid));
                                    }

                                });

                        ImageButton btDialogGuardar = dialog
                                .findViewById(R.id.bt_dialog_guardar);

                        final String precioOriginal = precio;

                        btDialogGuardar.setOnClickListener(v14 -> {

                            try {
                                if (!Inicio.gb.permitirPrecioLibreInferior()
                                        && !txtPrecio.getText().toString().equals("")
                                        && Double.parseDouble(txtPrecio.getText().toString()) < Double.parseDouble(precioOriginal)) {
                                    Notifica.mostrarToast(
                                            ComandaActivityV3.this,
                                            getString(R.string.mensaje_precio_inferior),
                                            2, Toast.LENGTH_LONG);
                                }

                                Inicio.gb.modLinea(position, Double.parseDouble(tvUnid.getText().toString().replace(",", ".")), txtObservaciones.getText()
                                                .toString(),
                                        txtPrecio.getText()
                                                .toString());

                                actualizarColeccionComanda();
                                listaComanda.setStackFromBottom(true);
                                modificarIconoSend();
                            } catch (Exception e) {
                                Log.e("ACTIVITY COMANDA", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
                            }

                            dialog.dismiss();
                        });

                        ImageButton btDialogCancelar = dialog.findViewById(R.id.bt_firma_cancelar);

                        btDialogCancelar.setOnClickListener(v13 -> dialog.dismiss());
                        ImageButton btMover = dialog.findViewById(R.id.btMover);
                        //CAMBIO BAMBI
                        txtPrecio.setText(precio);
                        if (tipo != Constantes.tipo_linea_recibida) {

                            btMover.setOnClickListener(v12 -> {
                                movLinea(position);
                                dialog.dismiss();
                            });

                            // para que no haya
                            // errores de conversión
                            if (Inicio.gb.cambiarPVP()) {
                                View layoutPrecio = dialog.findViewById(R.id.layout_cambio_precio);
                                layoutPrecio.setVisibility(View.VISIBLE);

                            }

                        }

                        ImageButton btDialogEliminar = dialog.findViewById(R.id.bt_dialog_eliminar);

                        final int tipoLinea = tipo;
                        btDialogEliminar.setOnClickListener(v1 -> {
                            if (tipoLinea == Constantes.linea_nueva_menu_maestro)
                                Inicio.gb.eliminarMenu(position,
                                        false);
                            else
                                Inicio.gb.eliminarLinea(position);
                            Notifica.mostrarToast(ComandaActivityV3.this, getString(R.string.mensaje_linea_eliminada), 1, Toast.LENGTH_SHORT);

                            actualizarColeccionComanda();
                            listaComanda.setStackFromBottom(true);
                            modificarIconoSend();
                            dialog.dismiss();
                        });
                        // Nuevo booleano
                        if (!Inicio.gb.isLineaAnulable(position)) {
                            btDialogEliminar.setVisibility(View.GONE);
                        }
                        /*
                         * LINEAS RECIBIDAS (NUEVAS MODIF) CONTROLAR QUE NO SE
                         * PUEDA CANCELAR CON UNIDADES EN NEGATIVO
                         * controlar nuevo boolean
                         */
                        if ((tipo == Constantes.tipo_linea_recibida && !Inicio.gb.anularComanda())
                                || (tipo == Constantes.tipo_linea_recibida && !Inicio.gb.anularPrefactura())) {
                            txtObservaciones.setEnabled(false);
                            btDialogRestar.setEnabled(false);
                            btDialogRestar.setText("");
                            btDialogEliminar.setVisibility(View.GONE);
                            btMover.setVisibility(View.GONE);
                            tvUnid.setEnabled(false);
                        } else if (tipo == Constantes.tipo_linea_recibida
                                && Inicio.gb.anularComanda()
                                || tipo == Constantes.tipo_linea_recibida
                                && Inicio.gb.anularPrefactura()) {
                            txtObservaciones.setEnabled(false);
                            btDialogRestar.setEnabled(false);
                            btDialogRestar.setText("");
                            btMover.setVisibility(View.GONE);

                        }

                        try {
                            dialog.setTitle(descripcion);
                            tvUnid.setText(unidades); // DEFINITIVO
                            txtObservaciones.setText(observacion);
                        } catch (Exception e) {
                            Log.e("ACTIVITY COMANDA",
                                    "Linea "
                                            + Thread.currentThread()
                                            .getStackTrace()[2]
                                            .getLineNumber() + ": "
                                            + e.getMessage());
                        }

                        dialog.show();

                        if (tipo == Constantes.linea_nueva_menu_maestro) {
                            View layoutBotonesUnidades = dialog
                                    .findViewById(R.id.layout_botones_unidades);
                            View layoutUnidades = dialog
                                    .findViewById(R.id.layout_unidades);
                            // btMover.setVisibility(View.GONE); //ESTO ES
                            // USABLE YA
                            layoutBotonesUnidades.setVisibility(View.GONE);
                            layoutUnidades.setVisibility(View.GONE);

                        }
                    } else {
                        // TEXTOS LIBRES
                        if (tipo == Constantes.tipo_linea_nueva
                                || Inicio.gb.anularComanda()) {
                            final int tipoLinea = tipo; // NUEVA
                            AlertDialog.Builder alert = new AlertDialog.Builder(
                                    ComandaActivityV3.this);
                            alert.setTitle(getString(R.string.alert_titulo_texto_libre));
                            alert.setMessage(getString(R.string.alert_texto_libre));
                            alert.setPositiveButton(
                                    getString(R.string.dialog_ok),
                                    (dialog, whichButton) -> {

                                        if (tipoLinea == Constantes.tipo_linea_recibida) { // NUEVA
                                            Inicio.gb
                                                    .eliminarLinea(position);
                                        } else {
                                            Inicio.gb
                                                    .getLineasVisiblesComanda()
                                                    .remove(position);
                                        }
                                        actualizarColeccionComanda();
                                        modificarIconoSend();
                                    });

                            alert.setNegativeButton(
                                    getString(R.string.dialog_cancelar),
                                    (dialog, whichButton) -> {
                                        // Canceled.
                                    });
                            alert.show();
                        }
                    }

                } else if (tipo == Constantes.linea_nueva_menu_maestro
                        || tipo == Constantes.linea_recibida_menu_maestro
                        && Inicio.gb.anularComanda() && unidadesDouble > 0) {

                    final boolean menuRecibido = tipo == Constantes.linea_recibida_menu_maestro;
                    AlertDialog.Builder alert = new AlertDialog.Builder(
                            ComandaActivityV3.this);
                    alert.setTitle(descripcion);
                    // alert.setMessage(getString(R.string.alert_eliminar));
                    alert.setPositiveButton(
                            getString(R.string.dialog_eliminar),
                            (dialog, whichButton) -> {
                                Inicio.gb.eliminarMenu(position,
                                        menuRecibido);
                                actualizarColeccionComanda();
                                modificarIconoSend();
                            });

                    alert.setNegativeButton(getString(R.string.txt_volver),
                            (dialog, whichButton) -> {
                                // Canceled.
                            });
                    alert.show();

                } else if (tipo == Constantes.linea_recibida_menu_detalle) {
                    Notifica.mostrarToast(ComandaActivityV3.this,
                            getString(R.string.dialog_no_editar_linea), 1,
                            Toast.LENGTH_SHORT);
                } else if (tipo == Constantes.linea_recibida_menu_maestro
                        || tipo == Constantes.tipo_linea_recibida
                        && unidadesDouble < 1) {
                    Notifica.mostrarToast(ComandaActivityV3.this,
                            getString(R.string.dialog_no_editar_linea), 1,
                            Toast.LENGTH_SHORT);

                } else if (tipo == Constantes.linea_nueva_menu_detalle) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(
                            ComandaActivityV3.this);

                    alert.setTitle(getString(R.string.titulo_dialog_texto_libre));
                    final EditText input = new EditText(ComandaActivityV3.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    alert.setView(input);

                    alert.setPositiveButton(getString(R.string.dialog_ok),
                            (dialog, whichButton) -> {
                                String value = "";
                                if (input.length() > 0) {
                                    value = input.getText().toString();
                                }
                                Inicio.gb
                                        .modLinea(position, 0.0, value, "");
                            });

                    alert.setNegativeButton(
                            getString(R.string.dialog_cancelar),
                            (dialog, whichButton) -> {
                                // Canceled.
                            });
                    alert.show();

                } else {
                    Notifica.mostrarToast(ComandaActivityV3.this,
                            getString(R.string.dialog_no_editar_linea), 1,
                            Toast.LENGTH_SHORT);
                }
            }

        }
    };

    private final AdapterView.OnItemLongClickListener listenerlongClickComanda = (ad, view, position, arg3) -> {

        try {
            if (Inicio.gb.getLineasVisiblesComanda().get(position)
                    .getInt("TIPO") == Constantes.tipo_linea_nueva

                    || Inicio.gb.getLineasVisiblesComanda().get(position)
                    .getInt("TIPO") == Constantes.linea_nueva_menu_maestro) {
                view.setBackgroundColor(Color.YELLOW);
                movLinea(position);
            } else {
                Notifica.mostrarToast(ComandaActivityV3.this,
                        getString(R.string.dialog_no_mover_linea), 1,
                        Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            Notifica.mostrarToast(ComandaActivityV3.this,
                    getString(R.string.dialog_no_mover_linea), 1,
                    Toast.LENGTH_SHORT);
            Log.e("ACTIVITY COMANDA",
                    "Linea "
                            + Thread.currentThread().getStackTrace()[2]
                            .getLineNumber() + ": "
                            + e.getMessage());
        }
        return true;

    };

    //endregion

    private void movLinea(int pos) {

        try {

            if (Inicio.gb.getLineasVisiblesComanda().get(pos).getInt("TIPO") != -1) {
                moviendoLineas = true;
                Inicio.gb.getLineasVisiblesComanda().get(pos)
                        .put("SELECCIONADA", 1);
                indiceLinea = pos;
                capaSubirLineas.setVisibility(View.VISIBLE);
                layoutGrids.setVisibility(View.GONE);
            } else {
                moviendoLineas = false;
                Notifica.mostrarToast(ComandaActivityV3.this,
                        getString(R.string.dialog_no_mover_linea), 1,
                        Toast.LENGTH_SHORT);
            }

        } catch (Exception e) {
            Log.e("ACTIVITY COMANDA", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
    }

    private void mostrarGridUnidades() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //ESTO SERÁ UNA DE LAS OPCIONES

        if (PreferenciasManager.usarTecladoUnidades()) {
            GridView gridUnidades = new GridView(ComandaActivityV3.this);
            gridUnidades.setNumColumns(Integer.parseInt(PreferenciasManager.getNumColumnas()));

            Ad_combo_unidades au = new Ad_combo_unidades(ComandaActivityV3.this);

            gridUnidades.setAdapter(au);
            gridUnidades
                    .setOnItemClickListener((parent, view, position, id) -> {

                        unidadesInsertar = Double.parseDouble(view.getTag()
                                .toString());
                        alert.dismiss();
                    });
            builder.setView(gridUnidades);
        } else {
            final EditText input = new EditText(ComandaActivityV3.this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            builder.setView(input);
            builder.setNegativeButton(getString(R.string.dialog_cancelar),
                    (dialog, whichButton) -> alert.dismiss());
            builder.setPositiveButton(getString(R.string.dialog_aceptar),
                    (dialog, whichButton) -> {
                        unidadesInsertar = Double.parseDouble(input.getText().toString());
                        alert.dismiss();
                    });
        }

        //HASTA AQUÍ
        builder.setTitle(getString(R.string.txt_unidades));
        builder.setNegativeButton(getString(R.string.dialog_cancelar),
                (dialog, which) -> dialog.dismiss());
        alert = builder.create();
        alert.show();

    }

    private void mostrarSelectorImpresora() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ListView listaImpresoras = new ListView(ComandaActivityV3.this);
        Ad_combo_impresoras ci = new Ad_combo_impresoras(ComandaActivityV3.this);

        listaImpresoras.setAdapter(ci);
        listaImpresoras
                .setOnItemClickListener((parent, view, position, id) -> {
                    //SI SELECCIONAMOS LA IMP BLUETOOTH
                    if (TpvConfig.getImpresoras()
                            .get(position).getDispositivo_windows().equals("BLUETOOTH")) {
                        new DescargarTxtTicket().execute();
                    } else {
                        ec = new EjecutarFuncion();
                        ec.execute("pedirCuenta", TpvConfig.getImpresoras()
                                .get(position).getDispositivo_windows());
                    }
                    alert.dismiss();
                });

        builder.setTitle(getString(R.string.titulo_combo_impresoras));
        builder.setView(listaImpresoras);
        builder.setNegativeButton(getString(R.string.dialog_cancelar),
                (dialog, which) -> dialog.dismiss());
        alert = builder.create();
        alert.show();

    }

    private class Cobrar extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }

    /*
     * HILO ASÍNCRONO ENVÍA LAS COMANDAS
     */

    private class EnviarComanda extends AsyncTask<Void, Void, Object> {


        @Override
        protected void onPreExecute() {
            enviandoComanda = true;
            Notifica.mostrarProgreso(ComandaActivityV3.this,
                    getString(R.string.dialog_enviando_comanda),
                    getString(R.string.dialog_espere) + "...", true);

        }

        @Override
        protected Void doInBackground(Void... params) {
            Inicio.gb.cargarJSONFunciones();
            //CAMBIO DE ORDEN DEL CLIENTE
            Inicio.gb.desbloquearMesa(Inicio.gb.getMesa()); //TODO NUEVO CAMBIO PARA VER SI ME PERMITE MANIPULAR EL CLIENTE DEL DOCUMENTO
            Inicio.gb.enviarComanda();
            if (Inicio.gb.getMesaActual().isClienteModificado()) //TODO PRUEBA DE CLIENTE DESPUÉS DE ENVIAR LA COMANDA
                Inicio.gb.enviarCliente();
            //nuevo cambio
            if (comensalesModificados)
                Inicio.gb.enviarComensales();
            Inicio.gb.enviarLineasEliminadas();
            Inicio.gb.cargarMesas(Inicio.gb.getIdRango(), false);
            Inicio.gb.ejecutarfuncion();
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            Notifica.mostrarProgreso(ComandaActivityV3.this, "", "", false);
            /*
             * if (Act_Comanda.pDialog != null){ Act_Comanda.pDialog.dismiss();
             * Act_Comanda.pDialog=null; }
             */
            if (Inicio.gb.isComandaEnviada()) {
                Inicio.gb.getLineasVisiblesComanda().clear();
                comensalesModificados = false;
                try {
                    Notifica.mostrarToast(ComandaActivityV3.this,
                            getString(R.string.mensaje_comandas_enviadas), 1,
                            Toast.LENGTH_SHORT);
                } catch (Exception e) {
                    Log.e("ACTIVITY COMANDA", "Linea "
                            + Thread.currentThread().getStackTrace()[2].getLineNumber()
                            + ": " + e.getMessage());
                }

            } else if (Inicio.gb.getError() == Constantes.error_conexion) {
                try {
                    Notifica.mostrarToast(ComandaActivityV3.this,
                            getString(R.string.mensaje_comandas_no_enviadas), 2,
                            Toast.LENGTH_LONG);
                } catch (Exception e) {
                    Log.e("ACTIVITY COMANDA", "Linea "
                            + Thread.currentThread().getStackTrace()[2].getLineNumber()
                            + ": " + e.getMessage());
                }
                //TODO Llamada para cuando no ha terminado de procesar la respuesta y acaba el timeout
                GestorLineas.Companion.guardarBackupComandaBD();
            } else {
                try {
                    Notifica.mostrarMensajeEstado(ComandaActivityV3.this);
                } catch (Exception e) {
                    Log.e("ACTIVITY COMANDA", "Linea "
                            + Thread.currentThread().getStackTrace()[2].getLineNumber()
                            + ": " + e.getMessage());
                }
            }
            Inicio.gb.setMesasOK(true);
            enviandoComanda = false;
            finish();
        }
    }


    class EjecutarFuncion extends AsyncTask<String, Void, Void> {

        String miFuncion = "";

        String impresora = ""; //PARA CONSERVAR EL NOMBRE DE LA IMPRESORA

        @Override
        protected Void doInBackground(String... params) {
            miFuncion = params[0];
            Inicio.gb.cargarJSONFunciones();
            switch (miFuncion) {
                case "cobrarCuenta":
                    Inicio.gb.cobrarCuenta(params[1]);
                    Inicio.gb.desbloquearMesa(Inicio.gb.getMesa());
                    finish();
                    break;
                case "pedirCuenta":
                    if (!TpvConfig.hayImpresoras()) {
                        Inicio.gb.pedirCuenta("");
                    } else {
                        Inicio.gb.pedirCuenta(params[1]);
                        impresora = params[1];
                    }
                    Inicio.gb.desbloquearMesa(Inicio.gb.getMesa());
                    break;
                case "pedirCuentaVerificada":
                    if (!TpvConfig.hayImpresoras()) {
                        Inicio.gb.pedirCuenta("");
                    } else {
                        Inicio.gb.pedirCuenta(params[1]);
                        impresora = params[1];
                    }
                    if (params[2].equals("false"))       //SI VAMOS A IMPRIMIR SIN VERIFICAR DESBLOQUEAMOS DIRECTAMENTE 0= NO VERIFICAR 1= VERIFICAR LINEAS
                        Inicio.gb.desbloquearMesa(Inicio.gb.getMesa());
                    break;
                case "setComensales":
                    Inicio.gb.enviarComensales();
                    break;
                case "enviarCliente":
                    Inicio.gb.enviarCliente();
                    break;
                case "desbloquearmesa":
                    Inicio.gb.desbloquearMesa(Inicio.gb.getMesa());
                    break;
                case "bloquearmesa":
                    Inicio.gb.bloquearMesa(Inicio.gb.getMesa());
                    break;
            }

            Inicio.gb.ejecutarfuncion();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (Inicio.gb.getResultado().equals("Ok")) {
                switch (miFuncion) {
                    case "pedirCuenta":
                        if (Inicio.gb.getResultado().toUpperCase().equals("OK")) {
                            try {
                                Notifica.mostrarToast(ComandaActivityV3.this, getString(R.string.mensaje_cuenta_pedida) + " " + Inicio.gb.getMesa(), 1, Toast.LENGTH_SHORT);
                            } catch (Exception e) {
                                Log.e("ACTIVITY COMANDA", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
                            }
                            finish();
                        } else if (Inicio.gb.getResultado().toUpperCase().equals("NUEVO MENSAJE QUE ME ENVIARÁ EL TPV PARA LINEAS = 0")) {
                            mostrarMensajeLineasCero(impresora);
                        }
                        break;
                    case "setComensales":
                        try {
                            Notifica.mostrarToast(ComandaActivityV3.this,
                                    getString(R.string.mensaje_comensales_guardados),
                                    1, Toast.LENGTH_SHORT);
                        } catch (Exception e) {
                            Log.e("ACTIVITY COMANDA", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
                        }
                        comensalesModificados = false;
                        break;
                    case "enviarCliente":
                        Inicio.gb.getMesaActual().setClienteModificado(false);
                        break;
                }
            } else {
                try {
                    Notifica.mostrarMensajeEstado(ComandaActivityV3.this);
                } catch (Exception e) {
                    Log.e("ACTIVITY COMANDA", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
                }
            }

        }
    }


    private void activarControl(AdapterView<?> adapterView, boolean activar) {
        adapterView.setEnabled(activar);
        btVolver.setEnabled(activar);
        btUnidades.setEnabled(activar);
    }

    private void mostrarMensajeLineasCero(final String impresora) {
        /*
        Mostramos dialog con mensaje de lineas a 0 y preguntamos si imprimir
        Crear nueva función en global pedircuentasinconfirmar para que el TPV saque la cuenta si o si
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(ComandaActivityV3.this);
        builder.setTitle("MENSAJE LINEAS A CERO");
        builder.setPositiveButton(android.R.string.yes, (dialog, id) -> {

            Inicio.gb.pedirCuentaVerificado(impresora, false);
            dialog.dismiss();
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //DETECTAMOS CUALQUIER TECLA. USADO PARA DETECTAR EL BOTÓN CENTRAL DE LOS AURICULARES

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {//PODEMOS ABRIR EL BUSCADOR DIRECTAMENTE EN MODO VOZ EN VEZ DE ACTIVAR LOS COMANDOS

            //PRIMERA OPCIÓN -> EJECUTAR COMANDOS
            //activarVoz();

            //SEGUNDA OPCIÓN -> ABRIR BUSCADOR EN MODO VOZ
            Intent i = new Intent(ComandaActivityV3.this, Buscador.class);
            i.putExtra("modo_voz", true);
            ComandaActivityV3.this.startActivityForResult(i, Constantes.REQUEST_BUSCAR_ARTICULOS);
            return true;
        }else if(keyCode==KeyEvent.KEYCODE_BACK){
            moviendoLineas = false;
            if (layoutGridFamilias.getVisibility() == View.GONE) { // Botón
                // atrás
                mostrarCapaArticulos(false);
            } else {
                cancelarPedido();
            }
        }
        return false;
    }


    //region PREFACTURA E IMPRESORA BLUETOOTH

    private void pedirCuenta() {
        /*
                SI HAY IMP BLUE CONFIGURADA IMPRIMIMOS POR ESA
                SI NO HAY, SEGUIMOS CON EL PROCEDIMIENTO NORMAL
                SI FALLA LA IMPRESORA BLUETOOTH, DESCONFIGURARLA Y SEGUIR CON EL PROCEDIMIENTO NORMAL
                 */


        if (Inicio.gb.getLineasVisiblesComanda().size() > 0) {
            if (!Inicio.gb.hayLineasNuevas()) {
                switch (PreferenciasManager.getModoUsoApp()) {
                    case Constantes.MODO_COMANDERO:
                        if (TpvConfig.hayImpresoras()
                                && PreferenciasManager.seleccionarImpresoraPrefactura()) {
                            mostrarSelectorImpresora();
                        } else {
                            if (PreferenciasManager.isPrefacturaBluetooth() && !PreferenciasManager.getImpresoraBluetooth().equals("")) {
                                new DescargarTxtTicket().execute(); //ESTO SE SUPONE QUE YA PONE LA MESA EN PREFACTURA
                            } else if (Inicio.gb.isVerificarPrefactura()) {
                                ec = new EjecutarFuncion();
                                ec.execute("pedirCuentaVerificada", "", "true");
                            } else {
                                ec = new EjecutarFuncion();
                                ec.execute("pedirCuenta", "");
                            }

                        }
                        break;
                    case Constantes.MODO_CLOUD:
                        if (!PreferenciasManager.getImpresoraBluetooth().equals("")) {
                            new DescargarTxtTicket().execute(); //ESTO SE SUPONE QUE YA PONE LA MESA EN PREFACTURA
                        } else {
                            Notifica.mostrarAlerta(ComandaActivityV3.this, "No hay impresora bluetooth configurada");
                        }
                        break;
                }


            } else {
                Notifica.mostrarToast(
                        ComandaActivityV3.this,
                        getString(R.string.mensaje_no_se_puede_pedir_cuenta),
                        2, Toast.LENGTH_SHORT);
            }
        } else {
            Notifica.mostrarToast(ComandaActivityV3.this,
                    getString(R.string.mensaje_comandas_sin_lineas), 1,
                    Toast.LENGTH_SHORT);
        }
    }

    private class DescargarTxtTicket extends AsyncTask<Void, Void, Void> {

        String txtTicket = "";

        @Override
        protected Void doInBackground(Void... params) {
            txtTicket = DownloadTicket.INSTANCE.getTicket();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!txtTicket.equals("")) {
                if (!new Print_Bluetooth().print(txtTicket)) {
                    //si falla la impresión bluetooth
                    if (PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_CLOUD)) {
                        Notifica.mostrarToast(ComandaActivityV3.this, "No se ha podido imprimir via bluetooth", 2, Toast.LENGTH_SHORT);
                    } else {
                        if (Inicio.gb.isVerificarPrefactura()) {
                            ec = new EjecutarFuncion();
                            ec.execute("pedirCuentaVerificada", "", "true");
                        } else {
                            ec = new EjecutarFuncion();
                            ec.execute("pedirCuenta", "");
                        }
                    }


                } else {
                    //si va bien
                    finish();
                }
            } else {
                //SI POR ALGUNA RAZÓN FALLA PEDIMOS LA CUENTA POR EL PROCEDIMIENTO NORMAL
                if (TpvConfig.hayImpresoras()
                        && PreferenciasManager.seleccionarImpresoraPrefactura()) {
                    mostrarSelectorImpresora();
                } else {
                    if (Inicio.gb.isVerificarPrefactura()) {
                        ec = new EjecutarFuncion();
                        ec.execute("pedirCuentaVerificada", "", "true");
                    } else {
                        ec = new EjecutarFuncion();
                        ec.execute("pedirCuenta", "");
                    }

                }
            }

        }
    }
    //endregion

    //region CONTROL BLOQUEO DE MESAS
    private void controlarBloqueo() {
        try {
            if (Inicio.gb.isMesaBloqueada()) {
                Notifica.mostrarToast(ComandaActivityV3.this, getString(R.string.mensaje_mesa_bloqueada), 2, Toast.LENGTH_SHORT);
                finish();
            }
        } catch (Exception e) {
            finish();
        }

    }

    //endregion

    //region VOZ

    @Override
    public void ejecutarComandoVoz(String com, String mesa) { //IGNORO LA MESA AQUÍ
        if (com.contains("cuenta")) {
            pedirCuenta();
        } else if (com.contains("enviar")) {
            enviarComanda();
        } else if (com.contains("cambiar")) {
            cambiarMesa();
        } else {
            //abrir buscador pasándole el parámetro de búsqueda
            Intent i = new Intent(ComandaActivityV3.this, Buscador.class);
            i.putExtra("busqueda", com.replace("buscar", ""));
            ComandaActivityV3.this.startActivityForResult(i, REQUEST_TEXT);
        }
    }

    //endregion

    //region CONTROL DE TAMAÑO COLECCIÓN DE COMANDA


    private void actualizarColeccionComanda() {
        try {
            //INTENTAREMOS DESDE AQUÍ CONTROLAR ESTA EXCEPCIÓN
            if (adComanda != null)
                adComanda.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region FINALIZAR MESA

    private class FinalizarDoc extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... strings) {
            Inicio.gb.cargarJSONFunciones();
            Inicio.gb.finalizarMesa();
            Inicio.gb.desbloquearMesa(Inicio.gb.getMesaActual().getNumero());
            Inicio.gb.ejecutarfuncion();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Si va bien, finalizamos activity aquí
            if (Inicio.gb.cerrarMesa())
                finish();
            super.onPostExecute(aVoid);
        }


    }

    //endregion

}
