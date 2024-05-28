package com.atecresa.preferencias;

import com.atecresa.application.R;

/**
 * Created by carlosr on 26/01/2017.
 * Clase para guardar algunos valores por defecto en el XML por si Android cierra la app
 */

public class Backup {

    //region MESAS BLOQUEADAS
    public static String[] getMesasBloqueadas() {
        if (!LectorXML.getStringValue(R.xml.backup, "mesas_bloqueadas").equals("")) {
            return LectorXML.getStringValue(R.xml.backup, "mesas_bloqueadas").split(",");
        } else
            return new String[0];
    }

    public static void guardarMesaBloqueada(String mesa) {
        LectorXML.setStringValue(R.xml.backup, "mesas_bloqueadas", mesa + "," + LectorXML.getStringValue(R.xml.backup, "mesas_bloqueadas"));
    }

    public static void eliminarMesasBloqueadas() {
        LectorXML.setStringValue(R.xml.backup, "mesas_bloqueadas", "");
    }

    //endregion

}
