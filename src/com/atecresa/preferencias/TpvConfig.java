package com.atecresa.preferencias;

import android.util.Log;

import com.atecresa.print.Impresora;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by carlosr on 20/07/2017.
 * OPCIONES POR DEFECTO QUE NOS ENVÍA EL TPV
 */

public class TpvConfig {

    //region COLORES
    private static int appForecolor = 0;
    private static int appBackColor = 0;

    public static int getAppForecolor() {
        return appForecolor;
    }

    public static int getAppBackColor() {
        return appBackColor;
    }

    public static void setAppForecolor(int _appForecolor) {
        appForecolor = _appForecolor;
    }

    public static void setAppBackColor(int _appBackColor) {
        appBackColor = _appBackColor;
    }
    //endregion COLORES

    //region CONFIG COMANDA

    private static String familiaTextosLibres = "";

    public static void setFamiliaTextosLibres(String familiaTextosLibres) {
        TpvConfig.familiaTextosLibres = familiaTextosLibres;
    }

    //endregion

    //region Impresoras

    private static ArrayList<Impresora> impresoras;

    public static ArrayList<Impresora> getImpresoras() {
        return impresoras;
    }

    public static boolean hayImpresoras() {
        return impresoras != null && impresoras.size() > 0;
    }

    public static void guardarImpresoras(JSONObject j) {

        impresoras = new ArrayList<>();

        try {
            for (int i = 0; i < j.getJSONObject("getImpresoras")
                    .getJSONArray("rows").length(); i++) {
                JSONObject imp = j.getJSONObject("getImpresoras")
                        .getJSONArray("rows").getJSONObject(i);
                if (imp.has("NOMBRE")) {
                    impresoras.add(new Impresora(imp.getString("NOMBRE"),
                            imp.getString("DEVICE")));
                } else {
                    break;
                }
            }
            //Impresora bluetooth
            if (!PreferenciasManager.getImpresoraBluetooth().equals("")){
                impresoras.add(0,new Impresora(PreferenciasManager.getImpresoraBluetooth(), "BLUETOOTH"));
            }
        } catch (JSONException e) {
            impresoras = null;
            Log.e("SISTEMA", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }

    }

    //endregion

}
