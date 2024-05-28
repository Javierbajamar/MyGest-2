package com.atecresa.comunicaciones.v3;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.atecresa.application.Inicio;
import com.atecresa.comunicaciones.ClienteSocket;
import com.atecresa.login.Operador;
import com.atecresa.preferencias.Constantes;
import com.atecresa.preferencias.PreferenciasManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
Clase para gestionar todas las consultas al TPV de forma centralizada
OPCIONAL EJECUTAR VIA SOCKET O VIA CLOUD
 */
public class JsonTPVQuery {

    private static JSONObject jsFunciones = new JSONObject();
    private static ListenerCnx listener;

    //region 1 CONSTRUCTOR
    public JsonTPVQuery(ListenerCnx cnxListenerK) {
        inicializarJSONFunciones();
        listener = cnxListenerK;
    }

    //endregion

    //region 2 FUNCIONES

    private void inicializarJSONFunciones() {
        JSONArray f;
        JSONObject s, o;
        Operador op = Inicio.gb.getOperadorActual();
        try {
            f = new JSONArray();
            s = new JSONObject().put("setserie", Inicio.gb.getNumeroSerie());
            o = new JSONObject().put("setoperador", op.getId() + ";" + op.getClave());
            f.put(s);
            f.put(o);
            jsFunciones.put("FUNCIONES", f);
        } catch (JSONException e) {
            Log.e("JSONQUERY", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
    }

    public void agregarFuncion(String nombre, String valor) {
        JSONObject funcion;
        try {
            funcion = new JSONObject();
            funcion.put(nombre, valor);
            jsFunciones.getJSONArray("FUNCIONES").put(funcion);
        } catch (JSONException e) {
            Log.e("JSONQUERY", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
    }

    public void sendQuery() {
        if (PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_CLOUD)) {
            //PETICIÒN HTTP
            executeHTTPQuery(Inicio.getContext()); //TODO Cuidado con este posible nulo
        } else {
            //PETICIÓN SOCKET
            new ExecuteSocketQuery(listener, jsFunciones).execute();
        }
    }

    //endregion

    //region 3 SOCKET

    private static class ExecuteSocketQuery extends AsyncTask<Void, Void, Void> {

        private ListenerCnx listenerK;
        private JSONObject jsFunciones;
        CnxResponseK response = new CnxResponseK("", 0, "0");

        ExecuteSocketQuery(ListenerCnx _listenerK, JSONObject _jsFunciones) {
            this.listenerK = _listenerK;
            this.jsFunciones = _jsFunciones;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                response.setResponse(ClienteSocket.ejecutar(
                        PreferenciasManager.getIp(), PreferenciasManager.getPuerto(), PreferenciasManager.getTimeOut(),
                        jsFunciones.toString() + "|END|"));
            } catch (Exception e) {
                response = null;    //TODO El control de errores debe de ser más complejo
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (response != null)
                listenerK.notifySuccess(response);
            else
                listenerK.notifyError(response);
        }
    }

    //endregion

    //region 4 HTTP REQUEST

    /*
    FUNCIÓN PARA PETICIONES GET HTTP. Integración Comandero Cloud
    La llamaremos desde las clases repositorio
     */
    private static void executeHTTPQuery(Context _ctx) {
        CnxVolley.sendGet(_ctx, PreferenciasManager.getUrlServidor(), jsFunciones.toString(), new ListenerCnx() {
            @Override
            public void notifySuccess(CnxResponseK response) {
                listener.notifySuccess(response);
            }

            @Override
            public void notifyError(CnxResponseK error) {
                listener.notifyError(error);
            }
        });

    }

    //endregion

}
