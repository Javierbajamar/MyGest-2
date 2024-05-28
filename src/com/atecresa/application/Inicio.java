package com.atecresa.application;

/*
 * CLASE ESTAT QUE TENDRa EN MEMORIA
 * EL OBJETO DE TIPO GLOBAL
 */

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.multidex.MultiDex;

import com.atecresa.gestionLineasComanda.GestorLineas;
import com.atecresa.preferencias.PreferenciasManager;
import com.atecresa.preferencias.Sistema;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Inicio extends Application {

    public static Global gb;
    private static Context ctx; //TODO Esto hay que quitarlo de aquí
    private static String nserie;
    public static boolean onDebug = false;

    public static void setOnDebug(boolean onDebug) {
        Inicio.onDebug = onDebug;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        llenarArrayUnidades();
        ctx = getApplicationContext();
        PreferenciasManager.cargarPreferencias();
        nserie = Sistema.getNumeroSerie(ctx); //MOVIDO AQUÍ PARA TENER SIEMPRE EL VALOR

        //TODO Prueba para inicializar gestor de lineas
        GestorLineas.Companion.setAplication(this);


    }

    public static void inicializarGlobal() {
        gb = new Global(nserie);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //VER UTILIDAD QUE PODEMOS DARLE A ESTE MÉTODO

    }

    private static int error = 0;

    public static int getError() {
        return error;
    }

    public static void setError(int error) {
        Inicio.error = error;
    }

    public static boolean hayRed() {
        ConnectivityManager connMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static void setContext(Context c) {
        ctx = c;
    }

    public static Context getContext() {
        return ctx;
    }

    private static String pantallaActual = "";

    public static String getPantallaActual() {
        return pantallaActual;
    }

    public static void setPantallaActual(String _pantallaActual) {
        pantallaActual = _pantallaActual;
    }

    public static String getNserie() {
        return nserie;
    }

    //region ARRAY UNIDADES

    private static ArrayList<String> arrayUnidades;

    public static ArrayList<String> getArrayUnidades() {
        return arrayUnidades;
    }

    private static void llenarArrayUnidades() {
        if (arrayUnidades == null) {
            arrayUnidades = new ArrayList<>();
            arrayUnidades.add("0.25");
            arrayUnidades.add("0.5");
            arrayUnidades.add("0.75");

            for (int i = 1; i < 11; i++) {
                arrayUnidades.add(i + "");
            }
        }
    }

    //endregion

    /*
     * Para agregar al identificador de operaciones
     */
    private static int autonum = 0;

    public static String getIdd() {
        autonum += 1;
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss",
                Locale.getDefault());
        return df.format(new Date())
                + String.format("%03d", autonum);
    }

    /*
     * Lo reinicio en el selector de mesas
     */
    public static void reiniciarContador() {
        autonum = 0;
    }


    //region MULTIDEX
    //CONTROL PARA CUANDO TENEMOS HABILITADO MULTIDEX. PARECE QUE NO SE INSTALA EN SDK<22
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
    //endregion

}
