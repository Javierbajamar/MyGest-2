package com.atecresa.comunicaciones.v3

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.atecresa.application.Inicio
import com.atecresa.comunicaciones.ClienteSocket
import com.atecresa.preferencias.Constantes
import com.atecresa.preferencias.PreferenciasManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/*
Clase para gestionar todas las consultas al TPV de forma centralizada
OPCIONAL EJECUTAR VIA SOCKET O VIA CLOUD
 */


class JsonTPVQueryK
(cnxListenerK: ListenerCnx) {

    //region 1 VARIABLES E INIT

    private var listener: ListenerCnx

    init {
        inicializarJSONFunciones()
        listener = cnxListenerK
    }

    //endregion

    //region 2 FUNCIONES JSON

    private fun inicializarJSONFunciones() {
        val f: JSONArray
        val s: JSONObject
        val o: JSONObject
        val op = Inicio.gb.operadorActual
        try {
            f = JSONArray()
            s = JSONObject().put("setserie", Inicio.gb.numeroSerie)
            o = JSONObject().put("setoperador", op.id + ";" + op.clave)
            f.put(s)
            f.put(o)
            jsFunciones.put("FUNCIONES", f)
        } catch (e: JSONException) {
            Log.e("JSONQUERY", "Linea " + Thread.currentThread().stackTrace[2].lineNumber
                    + ": " + e.message)
        }

    }

    fun agregarFuncion(nombre: String, valor: String) {
        val funcion: JSONObject
        try {
            funcion = JSONObject()
            funcion.put(nombre, valor)
            jsFunciones.getJSONArray("FUNCIONES").put(funcion)
        } catch (e: JSONException) {
            Log.e("JSONQUERY", "Linea "
                    + Thread.currentThread().stackTrace[2].lineNumber
                    + ": " + e.message)
        }

    }

    fun sendQuery() {
        if (PreferenciasManager.getModoUsoApp() == Constantes.MODO_CLOUD) {
            //PETICIÒN HTTP
            executeHTTPQuery(Inicio.getContext(), listener) //TODO Cuidado con este posible nulo
        } else {
            //PETICIÓN SOCKET
            ExecuteSocketQuery(listener, jsFunciones).execute()
        }
    }

    //endregion

    //region 3 SOCKET

    private class ExecuteSocketQuery
    internal constructor
    (val listenerK: ListenerCnx,val jsFunciones: JSONObject) : AsyncTask<Void, Void, Void>() {

        internal var response: CnxResponseK? = CnxResponseK("", 0, "0")

        override fun doInBackground(vararg voids: Void): Void? {
            try {
                response!!.response = ClienteSocket.ejecutar(
                        PreferenciasManager.getIp(), PreferenciasManager.getPuerto(), PreferenciasManager.getTimeOut(),
                        "$jsFunciones|END|")
            } catch (e: Exception) {
                response = null    //TODO El control de errores debe de ser más complejo
            }

            return null
        }

        override fun onPostExecute(aVoid: Void) {
            super.onPostExecute(aVoid)
            if (response != null)
                listenerK.notifySuccess(response!!)
            else
                listenerK.notifyError(response!!)
        }
    }

    companion object {

        private val jsFunciones = JSONObject()

        //endregion

        //region 4 HTTP REQUEST

        /*
    FUNCIÓN PARA PETICIONES GET HTTP. Integración Comandero Cloud
    La llamaremos desde las clases repositorio
     */
        private fun executeHTTPQuery(_ctx: Context, listener: ListenerCnx) {
            CnxVolley.sendGet(_ctx, PreferenciasManager.getUrlServidor(), jsFunciones.toString(), object : ListenerCnx {
                override fun notifySuccess(response: CnxResponseK) {
                    listener.notifySuccess(response)
                }

                override fun notifyError(response: CnxResponseK) {
                    listener.notifyError(response)
                }
            })

        }
    }

    //endregion

}
