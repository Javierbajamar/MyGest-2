package com.atecresa.gestionLineasComanda

/*
CLASE QUE GESTIONARÁ LAS COLECCIONES DE LINEAS EN LA BASE DE DATOS
SE PUEDE HACER UN REPOSITORIO MÁS EXTENSO PARA OBTENER POR SOCKET, PERO VAMOS A IR A LO CONCRETO
 */

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.atecresa.preferencias.Constantes
import org.json.JSONObject
import java.util.*

internal class RepositoryBDLineas
(application: Application) {  //REPOSITORIO PÚBLICO YA QUE ACCEDEREMOS A ÉL DESDE VARIOS VIEWMODEL

    //region 1 CONSTRUCTOR Y VARIABLES
    private val lDao: LineasDao
    val lineasPendientesEnvio: LiveData<List<LineasComanda>> //No hace falta el LiveData. Realmente
    val lineasRetenidas: LiveData<List<LineasComanda>>

    init {
        val ldb = RoomDatabaseLineas.getDatabase(application) //OBTENEMOS LA BASE DE DATOS
        lDao = ldb!!.lineasDao() //OBTENEMOS LA INTERFACE
        lineasPendientesEnvio = lDao.lineasPendientes
        lineasRetenidas = lDao.lineasRetenidas
    }

    //endregion

    //region 2 FUNCIONES GUARDADO BD

    fun guardarBDLineasPendientes(lineasComandaPendientes: List<JSONObject>) {
        val nuevasLineas = ArrayList<LineasComanda>()

        lineasComandaPendientes.forEach {
            nuevasLineas.add(LineasComanda(it.getString("MESA"), Constantes.linea_estado_pendiente_de_enviar, it.toString()))
        }
        GuardarAllLineasAsyncTask(lDao).execute(nuevasLineas)
    }

    fun guardarBDLineasRetenidas(num_mesa: String, lineas_comanda: List<JSONObject>) {
        val nuevasLineas = ArrayList<LineasComanda>()
        for (js in lineas_comanda) {
            nuevasLineas.add(LineasComanda(num_mesa, Constantes.linea_estado_retenida, js.toString()))
        }
        GuardarAllLineasAsyncTask(lDao).execute(nuevasLineas)
    }
    //endregion

    //region 3 FUNCIONES ELIMINAR EN BD
    //FUNCIÓN QUE ELIMINA LAS LINEAS DE LA BASE DE DATOS, PENDIENTES O RETENIDAS. EL TIPO LO RECIBE POR PARÁMETRO
    fun eliminarLineasPendientesBD() {
        EliminarLineaPendienteAsyncTask(lDao).execute() //TODO Filtrar si se van a borrar las pendientes o las retenidas
    }

    fun eliminarLineasRetenidas(mesa: String) {
        EliminarLineasRetenidasAsyncTask(lDao).execute(mesa)
    }
//endregion


    //region 4 ASYNCTASKS BD

    //HILO ASÍNCRONO PARA INSERTAR TODAS LAS LINEAS PENDIENTES EN LA BASE DE DATOS
    private class GuardarAllLineasAsyncTask internal constructor(private val mAsyncTaskDao: LineasDao) : AsyncTask<List<LineasComanda>, Void, Void>() {

        @SafeVarargs
        override fun doInBackground(vararg listaLineas: List<LineasComanda>): Void? {
            for (linea in listaLineas[0]) {
                mAsyncTaskDao.insertLinea(linea)
            }
            return null
        }
    }

    //HILO ASÍNCRONO PARA INSERTAR UNA LINEA EN LA BASE DE DATOS DE UNA EN UNA
    //TODO Puede que la usemos en un futuro, insertando con el estado LINEA DE FACTURACIÓN para ir insertando a medida
    //que vamos metiendo lineas en la comanda

    private class GuardarLineaAsyncTask internal constructor(private val mAsyncTaskDao: LineasDao) : AsyncTask<LineasComanda, Void, Void>() {

        override fun doInBackground(vararg linea: LineasComanda): Void? {
            mAsyncTaskDao.insertLinea(linea[0])
            return null
        }
    }

    //HILO ASÍNCRONO PARA BORRAR LAS LINEAS PENDIENTES EN LA BASE DE DATOS
    private class EliminarLineaPendienteAsyncTask internal constructor(private val mAsyncTaskDao: LineasDao) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg p0: Void?): Void? {
            mAsyncTaskDao.deleteLineasPendientes()
            return null
        }
    }

    //HILO ASÍNCRONO PARA BORRAR LAS LINEAS RETENIDAS EN LA BASE DE DATOS
    private class EliminarLineasRetenidasAsyncTask internal constructor(private val mAsyncTaskDao: LineasDao) : AsyncTask<String, Void, Void>() {
        override fun doInBackground(vararg _mesa: String): Void? {
            mAsyncTaskDao.deleteLineasRetenidas(_mesa[0])
            return null
        }
    }

    //endregion
}
