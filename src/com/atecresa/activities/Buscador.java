package com.atecresa.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;

import com.atecresa.adaptadoresUIBuscador.Ad_grid_busqueda_articulos;
import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.notificaciones.Notifica;
import com.atecresa.preferencias.Constantes;
import com.atecresa.preferencias.PreferenciasManager;
import com.atecresa.preferencias.TpvConfig;

import org.json.JSONException;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Buscador extends AppCompatActivity implements
        SearchView.OnQueryTextListener, TextToSpeech.OnInitListener {

    ActionBar actionBar;

    Ad_grid_busqueda_articulos adb;
    GridView gba;

    View fondoBusqueda;
    TextView tvTituloBusqueda;

    Intent i;

    //VOZ
    private TextToSpeech tts;
    private boolean voz = false;
    private static final int SPEECH_REQUEST_CODE = 1;
    private boolean mostrandoDialog = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        try {
            voz = Objects.requireNonNull(getIntent().getExtras()).getBoolean("modo_voz", false);
        } catch (Exception e) {
            Log.e("Act Buscador ",
                    "Linea "
                            + Thread.currentThread().getStackTrace()[2]
                            .getLineNumber() + ": "
                            + e.getMessage());
        }

        String busqueda = Objects.requireNonNull(getIntent().getExtras()).getString("busqueda", "");
        i = new Intent(Buscador.this, Buscador.class);

        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(TpvConfig.getAppBackColor());
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setIcon(R.drawable.ic_backspace_24dp);
        actionBar.setTitle(getString(R.string.txt_volver));

        this.fondoBusqueda = findViewById(R.id.layout_busqueda);
        this.fondoBusqueda.setBackgroundColor(TpvConfig.getAppBackColor());

        this.tvTituloBusqueda = findViewById(R.id.tv_titulo_busqueda);
        this.tvTituloBusqueda.setTextColor(TpvConfig.getAppForecolor());

        gba = findViewById(R.id.grid_Busqueda_Articulos);
        gba.setNumColumns(Integer.parseInt(PreferenciasManager.getNumColumnas()));

        Inicio.gb.buscarArticulos(busqueda);
        this.mostrarResultados();

        setResult(Activity.RESULT_CANCELED, i);

        //VOZ
        try {
            tts = new TextToSpeech(this, this);
            if (this.voz)
                mostrarDialogVoz();
        } catch (Exception e) {
            Log.e("Act Buscador ",
                    "Linea "
                            + Thread.currentThread().getStackTrace()[2]
                            .getLineNumber() + ": "
                            + e.getMessage());
        }

    }

    @Override
    protected void onResume() {
        Inicio.setContext(Buscador.this);
        super.onResume();
    }

    //region VOZ
    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            Locale loc = new Locale("ES", "", "");
            int result = tts.setLanguage(loc);

            if ((result == TextToSpeech.LANG_MISSING_DATA)
                    || (result == TextToSpeech.LANG_NOT_SUPPORTED)) {
                Log.e("TTS", "This Language is not supported");
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    //endregion

    SearchView mSearchView; //PARA CONTROLARLO DESDE TODA LA ACTIVITY

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_busqueda_articulos, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.requestFocus();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Buscador.this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onQueryTextChange(String articulo) {
        //Volver a activar esto para probar el control total por voz
        Inicio.gb.buscarArticulos(articulo.trim());
        this.mostrarResultados();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String articulo) {
        Inicio.gb.buscarArticulos(articulo.trim());
        this.mostrarResultados();
        return false;
    }

    private void mostrarResultados() {

        adb = new Ad_grid_busqueda_articulos(Buscador.this);
        gba.setAdapter(adb);
        gba.setOnItemClickListener(listenerGridBusquedaArticulos);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                Notifica.mostrarToastSimple(
                        Buscador.this,
                        getString(R.string.txt_add)
                                + " : "
                                + Objects.requireNonNull(data.getExtras()).getString("descripcionPlato"));
            }
        } else if (requestCode == SPEECH_REQUEST_CODE) {
            mostrandoDialog = false;
            List<String> results;
            String busqueda;
            try {
                results = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);

                busqueda = results.get(0);
            } catch (Exception e) {
                e.printStackTrace();
                busqueda = "";
            }

            if (!busqueda.equals("ok")) {
                this.mSearchView.setQuery(busqueda.trim(),false);
                //Inicio.gb.buscarArticulos(busqueda.trim());
                //this.mostrarResultados();
                tts.stop();
            } else
                finish();
        }

    }

    AdapterView.OnItemClickListener listenerGridBusquedaArticulos = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View v,
                                int position, long id) {
            try {
                if (Inicio.gb.getVinculos().hayVinculos()
                        && Inicio.gb.getVinculos().apilarVinculos(
                        Inicio.gb.getResultadosBusqueda().get(position))) {
                    Intent vi = new Intent(Buscador.this, Vinculos.class);
                    vi.putExtra("position", position);
                    vi.putExtra("isBusqueda", true);
                    Inicio.gb.getVinculos()
                            .setUnidadesMaestro(1);
                    vi.putExtra("unidadesInsertar", 1);
                    Buscador.this.startActivityForResult(vi, 0);
                } else if (Inicio.gb.getResultadosBusqueda().get(position)
                        .getString("T").equals("H")) {
                    Inicio.gb.agregarTextoLibre(position,
                            Constantes.lista_busqueda);
                    Notifica.mostrarToastSimple(
                            Buscador.this,
                            getString(R.string.txt_add)
                                    + " : "
                                    + Inicio.gb.getResultadosBusqueda()
                                    .get(position).getString("DES"));
                    //VOZ
                    if (voz)
                        mostrarDialogVoz();
                } else {
                    Inicio.gb.agregarNuevaLinea(position, 1,
                            Constantes.lista_busqueda);
                    Notifica.mostrarToastSimple(
                            Buscador.this,
                            getString(R.string.txt_add)
                                    + " : "
                                    + Inicio.gb.getResultadosBusqueda()
                                    .get(position).getString("DES"));
                    //VOZ
                    if (voz)
                        mostrarDialogVoz();
                }
                Intent res = new Intent();
                res.putExtra("activity", Constantes.activity_busqueda_articulos);
                setResult(Activity.RESULT_OK, res);
                actionBar.setIcon(R.drawable.ic_action_confirmar_comanda);
                actionBar.setTitle(getString(R.string.dialog_ok));

            } catch (JSONException e1) {
                Log.e("Act_Busqueda", "Error buscando");
            }
        }
    };


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_HEADSETHOOK:
                if (voz && !mostrandoDialog)
                    //Volver a poner si finalmente implementamos voz
                    mostrarDialogVoz();

                break;
            case KeyEvent.KEYCODE_BACK:
                Buscador.this.finish(); //TODO PROBAR ESTO
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //VOZ
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    private void mostrarDialogVoz() {
        mostrandoDialog = true;
        // VOZ
        if (tts == null)
            return;
        //tts.speak("¿Que plato busca?", TextToSpeech.QUEUE_ADD, null);
        //while (tts.isSpeaking()) {

        //}

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "¿Que plato busca?");
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }
}
