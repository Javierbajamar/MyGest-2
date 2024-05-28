package com.atecresa.gestionLineasComanda

import android.app.Application
import androidx.lifecycle.LiveData
import com.atecresa.comunicaciones.v3.CnxResponseK
import com.atecresa.comunicaciones.v3.JsonTPVQuery
import com.atecresa.comunicaciones.v3.ListenerCnx
import com.atecresa.preferencias.Constantes
import org.json.JSONObject

/*
CLASE PROPIETARIA DE LAS COLECCIONES DE LINEAS DE COMANDA, CON LAS QUE TRABAJAREMOS
EN UN FUTURO TENDREMOS EL OBJETO LINEA. POR AHORA TRABAJAMOS CON JSONOBJECT
ESTA CLASE HARÁ OPERACIONES QUE REALMENTE DEBERÍA DE HACER LA VIEWMODEL, PERO QUE POR AHORA MANTENDREMOS ASÍ
*/
class GestorLineas {

    companion object {
        //region 1 DECLARACIÓN LINEAS
        val VISIBLES = ArrayList<JSONObject>()  //Todas las lineas con las que estamos facturando
        val IDS_LINEAS_ELIMINADAS = ArrayList<String>() //Lineas ids de lineas de menu para eliminar
        private var PENDIENTES = ArrayList<JSONObject>()
        private var RETENIDAS = ArrayList<JSONObject>()
        //endregion

        //region 2 CONSTRUCTOR
        private var application: Application? = null

        private lateinit var repositoryBDLineas: RepositoryBDLineas

        fun setAplication(_application: Application) { //ESTO SERÁ UN PSEUDO CONSTRUCTOR
            application = _application
            repositoryBDLineas = RepositoryBDLineas(application!!)
        }

        //endregion

        //region 3 FUNCIONES OBSERVER

        //FUNCIÓN DEVUELVE UN BOOLEANO SI HAY LINEAS PENDIENTES, UNA VEZ COPIADAS A MEMORIA
        fun updateLineasPendientesFromBD(listaPendientes: List<LineasComanda>): Boolean {
            PENDIENTES.clear()
            listaPendientes.filter { l -> l.estado == Constantes.linea_estado_pendiente_de_enviar }
                    .forEach { PENDIENTES.add(JSONObject(it.json)) }
            return PENDIENTES.size > 0
        }

        fun updateLineasRetenidasFromBD(listaRetenidas: List<LineasComanda>) {
            RETENIDAS.clear()
            listaRetenidas.filter { l -> l.estado == Constantes.linea_estado_retenida }.forEach { RETENIDAS.add(JSONObject(it.json)) }
        }

        //endregion

        //region 4 BOOLEANOS

        fun hayLineasNuevas(): Boolean =
                VISIBLES.any { l ->
                    l.getInt("TIPO") == Constantes.tipo_linea_nueva ||
                            l.getInt("TIPO") == Constantes.linea_nueva_menu_maestro
                }

        fun hayLineasRetenidasEnMesa(mesa: String): Boolean =
                RETENIDAS.any { l -> l.getString("MESA") == mesa }

        //endregion

        //region 5 FUNCIONES GUARDADO EN BD
        //GUARDAMOS LO QUE ESTÁ PENDIENTE Y VACIAMOS LA LISTA DE LINEAS VISIBLES
        fun guardarBackupComandaBD() {
            repositoryBDLineas.guardarBDLineasPendientes(VISIBLES.filter { l ->
                l.getInt("TIPO") == Constantes.tipo_linea_nueva
                        || l.getInt("TIPO") == Constantes.linea_nueva_menu_maestro
                        || l.getInt("TIPO") == Constantes.linea_nueva_menu_detalle
            }) //GUARDA EN BD
            VISIBLES.clear()
        }

        fun guardarLineasRetenidasBD(_mesa: String) {
            repositoryBDLineas.guardarBDLineasRetenidas(_mesa, VISIBLES.filter { l ->
                l.getInt("TIPO") == Constantes.tipo_linea_nueva
                        || l.getInt("TIPO") == Constantes.linea_nueva_menu_maestro
                        || l.getInt("TIPO") == Constantes.linea_nueva_menu_detalle
            }) //GUARDA EN BD
            VISIBLES.clear()
        }

        //endregion

        //region 6 GETTERS

        fun getLiveDataPendientes(): LiveData<List<LineasComanda>> {
            return repositoryBDLineas.lineasPendientesEnvio
        }

        fun getLiveDataRetenidas(): LiveData<List<LineasComanda>> {
            return repositoryBDLineas.lineasRetenidas
        }


        fun getLineasPendienteEnvio(): ArrayList<JSONObject> {
            val pendientes = ArrayList<JSONObject>()
            repositoryBDLineas.lineasPendientesEnvio.value!!.forEach {
                pendientes.add(JSONObject(it.json))
            }
            return pendientes
        }

        //TODO Igual aquí podríamos leer de memoria
        fun getLineasRetenidas(_mesa: String): List<JSONObject> =
                RETENIDAS.filter { l -> l.getString("MESA") == _mesa }


        //endregion

        //region 7 FUNCIONES ELIMINACIÓN EN BD
        fun eliminarLineasPendientesBD() {
            PENDIENTES.clear()
            repositoryBDLineas.eliminarLineasPendientesBD() //TODO Filtrar si se van a borrar las pendientes o las retenidas
        }

        fun eliminarLineasRetenidas(mesa: String) {
            RETENIDAS.clear()
            repositoryBDLineas.eliminarLineasRetenidas(mesa)
        }

        //endregion

        //region 8 ENVIO DE LINEAS PENDIENTES AL TPV

        //ESTA FUNCIÓN TAMBIÉN TIENE QUE DESBLOQUEAR LA MESA

        fun sendLineasPendientes(listener: ListenerCnx) {

            val jq = JsonTPVQuery(object : ListenerCnx {
                override fun notifySuccess(response: CnxResponseK) {

                    try {
                        val rows = JSONObject(response.response).getJSONArray("ROWS")
                        for (i in 0 until rows.length()) {
                            val jo = rows.getJSONObject(i)
                            val iter = jo.keys()
                            while (iter.hasNext()) {
                                if ("setcomanda" == iter.next()) {
                                    if (jo.getJSONObject("setcomanda")
                                                    .getString("resultado").toUpperCase() == "OK") {
                                        listener.notifySuccess(CnxResponseK("", 0, ""))
                                        eliminarLineasPendientesBD()
                                    } else
                                        listener.notifyError(CnxResponseK("", 0, jo.getString("setcomanda")))
                                    break
                                }
                            }
                        }
                    } catch (e: Exception) {
                        listener.notifyError(CnxResponseK("", 0, e.localizedMessage))
                    }
                }

                override fun notifyError(response: CnxResponseK) {
                    listener.notifyError(CnxResponseK("", 0, response.error))
                }
            })

            PENDIENTES.forEach {
                var valores = (it.getString("MESA") + ";"
                        + it.getString("IDARTICULO") + ";"
                        + it.getString("UNID") + ";"
                        + it.getString("OBSERVACION"))
                valores += if (it.has("NUMLINEA") && it.has("NUMLINEAREF")
                        && it.has("TARIFA")) {
                    (";" + it.getString("NUMLINEA") + ";"
                            + it.getString("NUMLINEAREF"))
                } else {
                    ";;"
                }
                var tarifa = ""
                if (it.has("TARIFA")) {
                    tarifa = it.getString("TARIFA")
                }

                valores += if (it.has("PVPMODIFICADO")) {
                    if (it.has("PRECIO") && it.getString("PRECIO") != "") {
                        if (it.getDouble("PRECIO") > 0) {
                            (";" + tarifa + ";"
                                    + it.getString("PRECIO"))
                        } else {
                            ";0;" + it.getString("PRECIO")
                        }
                    } else {
                        ";$tarifa"
                    }
                } else {
                    ";$tarifa;"
                }


                if (it.has("IDD")) {
                    valores += ";" + it.getString("IDD")
                }
                // ESTO FALTABA AQUÍ, PUEDE SER EL ORIGEN DE LOS
                // PROBLEMAS
                if (it.has("IDTIPO_ENLACE")) {
                    valores += ";" + it.getString("IDTIPO_ENLACE")
                }
                jq.agregarFuncion("setcomanda", valores)
            }
            //DESBLOQUEO DE MESAS
            val mesas = ArrayList<String>()
            PENDIENTES.forEach { mesas.add(it.getString("MESA")) }
            mesas.distinct().forEach { jq.agregarFuncion("desbloquearmesa", it) }
            jq.sendQuery()
        }
    }


    //endregion
}



