package com.atecresa.util;

import android.annotation.SuppressLint;

import com.atecresa.preferencias.Constantes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Clase para las funciones de conversion y herramientas
 */

@SuppressLint("SimpleDateFormat")
public class Formateador {

    static final java.text.DecimalFormat formateador = new java.text.DecimalFormat("0.00");

    public static String formatearUds(double d) {
        if (d == Math.floor(d))
            return (int) d + "";
        else {
            d = Math.rint(d * 100) / 100;
            return formateador.format(d);
        }
    }

    public static double sumarUnidades(double _unid) {
        if (_unid < 1) {
            _unid += 0.25;
        } else {
            _unid += 1;
        }
        return _unid;
    }

    public static double restarUnidades(double _unid) {
        if (_unid < 1.1) {
            _unid -= 0.25;
            if (_unid == 0)
                _unid = 0.25;
        } else {
            _unid -= 1;
            if (_unid == 0)
                _unid = 1;
        }
        return _unid;
    }

    public static String formatearImporte(double d) {
        d = Math.rint(d * 100) / 100;
        return formateador.format(d);
    }

    public static String formatearImporteString(String _d) {
        double d = Double.parseDouble(_d.replace(",","."));
        d = Math.rint(d * 100) / 100;
        return formateador.format(d);
    }

    public static String formatearMacAdress(String s) {
        s = s.replace(":", "").replace(".", "");
        return s;
    }

    public static int getTiempoDeEsperaMesa(String tiempoMesa) {
        int horas = 0;
        int minutos;
        if (tiempoMesa.contains("h")) {
            horas = Integer.parseInt(tiempoMesa.split(" ")[0].replace("h", ""));
            minutos = Integer.parseInt(tiempoMesa.split(" ")[1]
                    .replace("m", ""));
        } else {
            minutos = Integer.parseInt(tiempoMesa.replace("m", ""));
        }
        if (horas > 0 || minutos > 15)
            return Constantes.tiempo_con_retraso_grave;
        else if (minutos > 5)
            return Constantes.tiempo_con_retraso;
        else
            return Constantes.tiempo_ok;

    }

    private static int numLinea = 0;

    private static int numLineaDetalle = 0;

    public static void reiniciarContadorLineas() {
        numLinea = 0;
        numLineaDetalle = 0;
    }

    public static int getNumLinea() {
        numLinea += 100;
        return numLinea;
    }

    public static int getNumLineaDetalle() {
        numLineaDetalle += 1;
        return numLinea + numLineaDetalle;
    }

    public static class ComparadorJSON implements Comparator<JSONObject> {

        public int compare(JSONObject o1, JSONObject o2) {

            try {
                return (Integer.compare(o1.getInt("NLINEA"), o2.getInt("NLINEA")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    /*
    FUNCIÓN QUE BUSCAR EN LOS DOS PRIMEROS ELEMENTOS DEL ARRAY RECIBIDO POR VOZ
    UN NÚMERO, EL QUE LO TENGA, LO DEVOLVEMOS EN UNA LISTA
     */
    public static String getNumString(String cad1, String cad2) {
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(cad1);
        if (m.find()) {
            return m.group();
        }
        m = p.matcher(cad2);
        if (m.find()) {
            return m.group();
        }
        return "";
    }


}
