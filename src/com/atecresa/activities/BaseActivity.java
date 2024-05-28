package com.atecresa.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.atecresa.preferencias.Constantes;
import com.atecresa.preferencias.PreferenciasManager;

import java.util.Locale;

/**
 * Created by carlos on 29/01/2018.
 * Interfaz con las implementaciones básicas para todas las activities
 */

public abstract class BaseActivity extends AppCompatActivity {

    //region VOZ

    public void activarVoz() {
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

    public abstract boolean onKeyDown(int keyCode, KeyEvent event);

    public abstract void ejecutarComandoVoz(String com,String mesa);

    protected abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    //endregion

    //region HILO ASÍNCRONO

    public static abstract class EjecutarFuncion extends AsyncTask<String, Void, Void>{}

    //endregion

}
