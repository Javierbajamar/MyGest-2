package com.atecresa.gestionCobros.tpv

import androidx.lifecycle.MutableLiveData
import com.atecresa.application.Inicio
import com.atecresa.comunicaciones.v3.CnxResponseK
import com.atecresa.comunicaciones.v3.JsonTPVQuery
import com.atecresa.comunicaciones.v3.ListenerCnx
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

/*
CLASE CON LA LISTA ESTÁTICA DE FORMAS DE PAGO Y CON LA FUNCIÓN PARA ENVIAR COBROS AL TPV
 */
class RepositoryCobrosTPV {
    //region 0 INIT
    init {
        items.value = listaFormasDePagoTPVS //Hago esto aquí, porque la carga de la lineas de cobro las haré desde Global.
    }

    //endregion
    companion object {
        //region 1 LISTA DE FORMAS DE PAGO
        private var listaFormasDePagoTPVS: ArrayList<FormaPagoTPV> = ArrayList()
        val items: MutableLiveData<java.util.ArrayList<FormaPagoTPV>> = MutableLiveData()
        //endregion

        //region 2 DESCARGA Y GUARDADO DE FORMAS DE PAGO
        /*
        POR AHORA NO USAMOS ESTA FUNCIÓN, YA QUE DESCARGAMOS LA LISTA DE FORMAS DE PAGO DESDE GLOBAL
         */
        private fun getFormasPagoTPV() {
            //new DescargaFormasPago().execute();
            val jq = JsonTPVQuery(object : ListenerCnx {
                override fun notifySuccess(response: CnxResponseK) {
                    try {
                        guardarFormasPagoTPV(JSONObject(response.response).getJSONArray("ROWS").toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun notifyError(response: CnxResponseK) {

                }
            })
            jq.agregarFuncion("getTratamientosCobro", "")
            jq.sendQuery()
        }

        //FUNCIÓN PÚBLICA PARA ACCEDER DESDE GLOBAL TAMBIÉN. PARA EL NUEVO APARTADO DE COBROS DEL TPV
        //SEGURAMENTE HABRÁ QUE PARSEAR ALGO DESDE FUERA, PRIMERO
        fun guardarFormasPagoTPV(jsonArray: String): Boolean {
            return try {
                val listType = object : TypeToken<List<FormaPagoTPV>>() {}.type
                items.value = Gson().fromJson(jsonArray, listType) //LE PASAMOS LA LISTA AL LIVEDATA
                !items.value.isNullOrEmpty()
            } catch (e: Exception) {
                false
            }
        }


        //endregion

        //region 3 ENVÍO DE COBRO AL TPV
        //TODO debemos de cambiar este método para enviar la firma también
        fun sendPayment(_total: String, _idTratamiento: String, _codeTransaction: String, firma: String?, listenerCobrosTPV: ListenerCobrosTPV) {
            //TODO Estas tres variables guardarlas en la clase sistema y llenar los campos directament
            val jq = JsonTPVQuery(object : ListenerCnx {
                override fun notifySuccess(response: CnxResponseK) {
                    //TODO Procesar pago con la función guardar devolución
                    listenerCobrosTPV.notifyPaymentSuccess(Inicio.gb.sePuedeFinalizarTicket())
                }

                override fun notifyError(response: CnxResponseK) {
                    listenerCobrosTPV.notifyPaymentError("algo que de fallo")
                }
            })
            jq.agregarFuncion("desbloquearmesa", Inicio.gb.mesaActual.numero)
            //TODO AGREGAR FIRMA. No sé si en la linea de cobro en la cabecera con un setFirma
            if (firma != "") jq.agregarFuncion("setFirma", firma) //GUSTAVO DEBE DE IMPLEMENTAR LA FUNCIÓN
            jq.agregarFuncion("setCobro", Inicio.gb.mesaActual.numero + ";" + _idTratamiento + ";" + _total + ";" + _codeTransaction)
            jq.sendQuery()
        }
        //endregion
    }


}