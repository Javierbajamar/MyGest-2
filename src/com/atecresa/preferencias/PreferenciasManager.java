package com.atecresa.preferencias;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.atecresa.application.Inicio;
import com.atecresa.application.R;


/**
 * Created by carlosr on 14/07/2016.
 * OPCIONES CONFIGURADAS POR EL USUARIO
 */
public class PreferenciasManager {

    private static Float tam_Fuente;
    private static Float tam_Fuente_Titulo;

    //METER VARIABLES EN MEMORIA

    @SuppressLint("HardwareIds")
    public static void cargarPreferencias() {
        tam_Fuente = Float.parseFloat(getTamFuente());
        tam_Fuente_Titulo = Float.parseFloat(getTamFuenteTitulo());

        //INICIALIZAMOS OPCIONES DE CONEXIÓN
        _ip = LectorXML.getStringValue(R.xml.preferencias, "ip").trim();

        try {
            if (LectorXML.getStringValue(R.xml.preferencias, "puerto").equals(""))
                _puerto = 5000;
            else
                _puerto = Integer.parseInt(LectorXML.getStringValue(R.xml.preferencias, "puerto"));
        } catch (NumberFormatException e) {
            _puerto = 5000;
        }
        _uuid = LectorXML.getStringValue(R.xml.preferencias, "uuid");

        modoUsoApp = LectorXML.getStringValue(R.xml.preferencias, "modo_mygest");
        _url = LectorXML.getStringValue(R.xml.preferencias, "servidor");
        try {
            if (isEmulator())
                nserie = "123456789";
            else
                nserie = Settings.Secure.getString(Inicio.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            nserie = "";
        }

        modo_una_mano=LectorXML.getBooleanValue(R.xml.preferencias,"modo_una_mano");
    }

//REGION serial

    private static String nserie = "";

    private static String getNumeroSerie(Context ctx) {
        return nserie;
    }

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }
    //endregion

//region CONEXIÓN CON EL TPV

    private static String _ip = "";
    private static int _puerto = 0;

    public static String getIp() {
        return _ip;
    }

    public static int getPuerto() {
        return _puerto;
    }

    public static int getTimeOut() {
        try {
            if (LectorXML.getStringValue(R.xml.preferencias, "timeout").equals(""))
                return 15;
            else
                return Integer.parseInt(LectorXML.getStringValue(R.xml.preferencias,
                        "timeout"));
        } catch (NumberFormatException e) {
            return 15;
        }
    }
    //endregion

    //region IMPRESORAS

    private static boolean _prefactura_bluetooth = false;

    public static boolean isPrefacturaBluetooth() {
        return _prefactura_bluetooth;
    }

    public static void setPrefacturaBluetooth(boolean pb) {
        _prefactura_bluetooth = pb;
    }

    public static boolean seleccionarImpresoraPrefactura() {
        return LectorXML.getBooleanValue(R.xml.preferencias, "seleccionar_impresora_prefactura");
    }

    public static String getImpresoraBluetooth() {
        return LectorXML.getStringValue(R.xml.preferencias, "opt_bluetooth");
    }

    public static void setImpresoraBluetooth(String impresoraBluetooth) {
        LectorXML.setStringValue(R.xml.preferencias, "opt_bluetooth", impresoraBluetooth);
    }
    //endregion

    //region  OPCIONES COMANDA
    public static boolean tecladoFijo() {
        return LectorXML.getBooleanValue(R.xml.preferencias, "teclado_fijo_familias");
    }

    public static boolean notificarSonido() {
        return LectorXML.getBooleanValue(R.xml.preferencias, "notificaciones_con_sonido");
    }


    public static boolean usarComandoVoz() {
        return LectorXML.getBooleanValue(R.xml.preferencias, "comandos_de_voz");
    }

    public static String getNumColumnas() {
        String n = LectorXML.getStringValue(R.xml.preferencias, "num_columnas");
        if (n.equals("") || n.equals("0"))
            return "3";
        else
            return n;
    }

    public static int getAnchoColumnasCocina() {
        String n = LectorXML.getStringValue(R.xml.preferencias, "tam_ancho_columnas_cocina");
        if (n.equals("") || n.equals("0"))
            return 250;
        else
            return Integer.parseInt(n);
    }

    public static int getAltoColumnasCocina() {
        String n = LectorXML.getStringValue(R.xml.preferencias, "tam_alto_celda_cocina");
        if (n.equals("") || n.equals("0"))
            return 250;
        else
            return Integer.parseInt(n);
    }

    //endregion

    //region TAMAÑO FUENTES VIGILAR CAMBIOS

    public static Float getTamFuenteNew() {
        return tam_Fuente;
    }

    public static Float getTamTituloNew() {
        return tam_Fuente_Titulo;
    }

    private static String getTamFuente() {
        String n = LectorXML.getStringValue(R.xml.preferencias, "tam_fuente");
        if (n.equals("") || n.equals("0")) {
            return "13";
        } else {
            return n;
        }
    }


    private static String getTamFuenteSubtitulo() {
        String n = LectorXML.getStringValue(R.xml.preferencias, "tam_fuente");
        if (n.equals("") || n.equals("0")) {
            return "11";
        } else {
            String size = Integer.parseInt(n) - 6 + "";
            if (size.equals("0"))
                return n;
            else
                return size;
        }
    }


    private static String getTamFuenteTitulo() {
        String n = LectorXML.getStringValue(R.xml.preferencias, "tam_fuente_titulo");
        if (n.equals("") || n.equals("0")) {
            return "50";
        } else {
            return n;
        }
    }
    //endregion

    //region USO APP

    private static String modoUsoApp = "0";

    /*
    0 COMANDERO / 1 COCINA / 2 CLOUD
     */
    public static String getModoUsoApp() {
        return modoUsoApp;
    }

    //endregion

    //region TECLADO UNIDADES

    public static boolean usarTecladoUnidades() {
        return LectorXML.getBooleanValue(R.xml.preferencias, "teclado_unidades");
    }

    //endregion

    //region SELECCIÓN DE CLIENTES
    public static boolean puedeSeleccionarCliente() {
        return LectorXML.getBooleanValue(R.xml.preferencias, "seleccionar_cliente");
    }

    //endregion

    //region PLATAFORMA DE PAGO

    public static String getPagoEmail() {
        return LectorXML.getStringValue(R.xml.preferencias, "pago_email");
    }

    public static String getPagoPhone() {
        return LectorXML.getStringValue(R.xml.preferencias, "pago_phone");
    }

    public static String getPagoPassword() {
        return LectorXML.getStringValue(R.xml.preferencias, "pago_password");
    }

    public static boolean canarypayConfigurado() {
        return !getPagoEmail().equals("")
                //&&!getPagoPhone().equals("") //Es opcional
                && !getPagoPassword().equals("")
                && !getServidorPago().equals("");
    }

    public static String getServidorPago() {
        return LectorXML.getStringValue(R.xml.preferencias, "pago_servidor");
    }


    //endregion

    //region COCINA

    public static boolean confirmarPlatoDosPasos() {
        return LectorXML.getStringValue(R.xml.preferencias, "pasos_cocina").equals("2");
    }

    //endregion

    //region BOOLEANO FACTURACIÓN

    private static boolean facturando = false;

    public static boolean isFacturando() {
        return facturando;
    }

    public static void setFacturando(boolean facturando) {
        PreferenciasManager.facturando = facturando;
    }

    //endregion

    //region SERVIDOR CLOUD

    private static Boolean isSSH() {
        return LectorXML.getBooleanValue(R.xml.preferencias, "pref_ssh");
    }

    private static String getHTTP() {
        if (isSSH())
            return "https://";
        else
            return "http://";
    }

    //TODO Cargado en memoria
    private static String _url = "ategestservidores.net"; //Inicializo aquí por si acaso.

    public static String getUrlServidor() {
        return getHTTP() + _url + "/AtegestServ/ServiciosComandero?";
    }

    public static String getUrlServidorGZIP() {
        return getHTTP() + _url + "/AtegestServ/ServiciosComandero?";
    }

    //endregion

    //region INFORMACIÓN DE LA EMPRESA EN LA NUBE

    private static String _uuid = "";

    public static String getUuid() {
        return _uuid;
    }

    public static void setUuid(String nuevoUuid) {
        LectorXML.setStringValue(R.xml.preferencias, "uuid", nuevoUuid);
    }

    //endregion

    //region MODO UNA MANO

    private static boolean modo_una_mano=false;

    public static boolean isModoUnamano() {
        return modo_una_mano;
    }

    public static void setModo_una_mano(boolean modo_una_mano) {
        PreferenciasManager.modo_una_mano = modo_una_mano;
        LectorXML.setBooleanValue(R.xml.preferencias, "modo_una_mano", modo_una_mano);
    }

    //endregion
}
