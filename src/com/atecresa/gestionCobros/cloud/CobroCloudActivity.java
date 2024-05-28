package com.atecresa.gestionCobros.cloud;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atecresa.activities.BaseActivity;
import com.atecresa.adaptadoresUIComanda.Ad_grid_forma_pago;
import com.atecresa.adaptadoresUIComanda.RecyclerTouchListener;
import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.preferencias.TpvConfig;
import com.atecresa.print.ActionBarDialog;
import com.atecresa.util.Formateador;

public class CobroCloudActivity extends BaseActivity {

    private EditText edEntrega;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cobros_tpv_activity);
        if (getSupportActionBar()!=null){ //TODO Controlar pq es null
            getSupportActionBar().setTitle("Pendiente: " + Formateador.formatearImporteString(Inicio.gb.getMesaActual().getTotalPendiente()));
            getSupportActionBar().setSubtitle("Mesa " + Inicio.gb.getMesa());
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            ColorDrawable colorDrawable = new ColorDrawable();
            colorDrawable.setColor(TpvConfig.getAppBackColor());
            getSupportActionBar().setBackgroundDrawable(colorDrawable);
        }


        edEntrega = findViewById(R.id.etEntrega);
        llenarGrid();
    }

    private void llenarGrid() {
        RecyclerView mRecyclerView = findViewById(R.id.rvPagos);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3); //3 ES EL NÚMERO DE COLUMNAS
        RecyclerView.Adapter mAdapter = new Ad_grid_forma_pago(Inicio.gb.getListaFormasPago());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(CobroCloudActivity.this, mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                //AQUÍ YA TENDREMOS LA FORMA DE PAGO CON EL position de la lista Y LLAMAREMOS AL ASYNTACSK enviado en id
                //Toast.makeText(Act_Cobro.this, "Entrega: " + edEntrega.getText().toString() + " € en " + lfp.get(position).getDescripcion(), Toast.LENGTH_LONG).show();
                //mostrarDialogCobro(Inicio.gb.getListaFormasPago().get(position).getForma(),"150","200","50");

                new EnviarCobro().execute(edEntrega.getText().toString(), Inicio.gb.getListaFormasPago().get(position).getId());

            }

            @Override
            public void onLongClick(View view, int position) {
                //Toast.makeText(Act_Cobro.this, "long click del " + position, Toast.LENGTH_LONG).show();
            }


        }));

    }

    @Override
    public void activarVoz() {
        super.activarVoz();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void ejecutarComandoVoz(String com, String mesa) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    //region COBROS Y FINALIZACIÓN

    private class EnviarCobro extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... pam) {
            //IMPORTE, IDTRATAMIENTO
            Inicio.gb.cargarJSONFunciones();
            Inicio.gb.enviarCobro(pam[0], pam[1]);
            Inicio.gb.ejecutarfuncion();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //TODO DAR OPCIÓN A ENVIAR FACTURA POR PDF AL EMAIL
            //SI EL PENDIENTE ES =0 YA PODREMOS FINALIZAR LA MESA Y CERRARLA. LA FINALIZAREMOS CUANDO RECIBAMOS EL RESULT OK EN LA ACTIVITY COMANDA
            //TODO SI LE DAMOS AL BOTÓN HACIA ATRÁS O CERRAR VENTANA (HAY QUE AÑADIR ESE BOTÓN) DAMOS UN RESULT CANCEL
            if (Inicio.gb.sePuedeFinalizarTicket()) {
                mostrarDialogCobro(Inicio.gb.getDevolucion().getConcepto(),
                        Inicio.gb.getDevolucion().getTotalImporte(),
                        edEntrega.getText().toString(),
                        Inicio.gb.getDevolucion().getDevolucion());

            } else if (Inicio.gb.getDevolucion() != null) {
                getSupportActionBar().setTitle("Pendiente: "
                        + Formateador.formatearImporteString(Inicio.gb.getDevolucion().getTotalPendiente()) + " €");
            } else {
                Toast.makeText(CobroCloudActivity.this, "Error envíando el cobro", Toast.LENGTH_LONG).show();
            }
            edEntrega.setText("");
            super.onPostExecute(aVoid);
        }
    }

    //endregion

    //region DIALOG COBRO

    private void mostrarDialogCobro(String formaPago, String importe, String entrega, String devolucion) {
        Bundle args = new Bundle();
        args.putString("forma_de_pago", formaPago);
        args.putString("importe", importe);
        args.putString("entrega", entrega);
        args.putString("devolucion", devolucion);
        ActionBarDialog actionbarDialog = new ActionBarDialog();
        actionbarDialog.setContext(CobroCloudActivity.this); //FUNDAMENTAL
        actionbarDialog.setArguments(args);
        actionbarDialog.show(getSupportFragmentManager(),
                "action_bar_frag");
    }

    //endregion


    //region MENU Y KEYDOWN
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_cobro, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cobro_cancelar:
                volverAComanda();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                volverAComanda();
                return false;
        }
        return false;
    }

    private void volverAComanda(){
        Intent c=new Intent();
        if (Inicio.gb.getDevolucion()!=null){
            Inicio.gb.getMesaActual().setTotalPendiente(Inicio.gb.getDevolucion().getTotalPendiente());
        }
        setResult(Activity.RESULT_CANCELED,c);
        finish();
    }

    //endregion
}
