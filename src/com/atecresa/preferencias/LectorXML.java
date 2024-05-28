package com.atecresa.preferencias;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.atecresa.application.Inicio;

/**
 * Created by carlosr on 17/02/2017.
 * CLASE PARA LEER Y ESCRIBIR EN LOS DIFERENTES XML
 */

public class LectorXML {

    public LectorXML() {
    }

    static String getStringValue(int idXml, String key) {
        try {

            PreferenceManager.setDefaultValues(Inicio.getContext(),
                    idXml, true);
            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Inicio.getContext().getApplicationContext());
            return p.getString(key, "");
        } catch (Exception e) {
            Log.e("Inicio ", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
            return "";
        }
    }

    static int getIntValue(int idXml, String key) {
        try {
            PreferenceManager.setDefaultValues(Inicio.getContext(),idXml, true);
            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Inicio.getContext().getApplicationContext());
            return p.getInt(key, -1);
        } catch (Exception e) {
            Log.e("Inicio ", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
            return -1;
        }
    }

    //Extrae un valor de tipo booleano de las preferencias
    static boolean getBooleanValue(int idXml, String key) {
        try {
            PreferenceManager.setDefaultValues(Inicio.getContext(),idXml, true);
            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Inicio.getContext().getApplicationContext());
            return p.getBoolean(key, false);
        } catch (Exception e) {
            return false;
        }
    }

    static void setStringValue(int idXml, String key, String value) {

        PreferenceManager.setDefaultValues(Inicio.getContext(),idXml, false);
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Inicio.getContext().getApplicationContext());
        SharedPreferences.Editor editor = p.edit();
        editor.putString(key, value);
        editor.apply();

    }

    static void setIntValue(int idXml, String key, int value) {

        PreferenceManager.setDefaultValues(Inicio.getContext(),idXml, false);
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Inicio.getContext().getApplicationContext());
        SharedPreferences.Editor editor = p.edit();
        editor.putInt(key, value);
        editor.apply();

    }

    static void setBooleanValue(int idXml, String key, boolean value){
        PreferenceManager.setDefaultValues(Inicio.getContext(),idXml, false);
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Inicio.getContext().getApplicationContext());
        SharedPreferences.Editor editor = p.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}
