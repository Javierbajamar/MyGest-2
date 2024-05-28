package com.atecresa.gestionCobros.tpv

/*
CLASE PARA ENVIAR EL COBRO AL TPV
CON UNA INTERFAZ LISTENER PARA RECOGER EL RESULTADO EN LA CLASE QUE LA LLAME
PREPARADA PARA USAR DESDE TODAS LAS ACTIVITIES QUE NECESITEN ENVIAR UNA DETERMINADA FORMA DE PAGO
 */

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.atecresa.application.Inicio
import com.atecresa.comunicaciones.v3.CnxResponseK
import com.atecresa.comunicaciones.v3.JsonTPVQuery
import com.atecresa.comunicaciones.v3.ListenerCnx
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/*
CLASE PARA ENVIAR EL COBRO AL TPV
 */
class ViewModelCobrosTPV (_pl: ListenerCobrosTPV) : ViewModel() {

    //region 1 VARIABLES E INICIALIZADOR
    private var listenerCobrosTPV: ListenerCobrosTPV = _pl    //LISTENER PARA COMUNICAR EL RESULTADO DE LA TRANSACCIÓN A LA ACTIVITY
    private var listaFormasDePagoTPVS: ArrayList<FormaPagoTPV> = ArrayList()
    private var data: MutableLiveData<ArrayList<FormaPagoTPV>> = MutableLiveData()

    val items: MutableLiveData<ArrayList<FormaPagoTPV>>
        get() = data

    init {
        //INICIALIZAMOS LISTAS
        //INICIALIZAMOS LIVEDATA PARA USAR EL OBSERVER EN LA ACTIVITY
        data.value = listaFormasDePagoTPVS //ASIGNAMOS LA LISTA AL LIVEDATA
        loadPayments() //CARGAMOS LAS FORMAS DE PAGO
    }

    //endregion

    //region 2 DESCARGA DE FORMAS DE PAGO
    private fun loadPayments() {

        //new DescargaFormasPago().execute();
        val jq = JsonTPVQuery(object : ListenerCnx {
            override fun notifySuccess(response: CnxResponseK) {

                try {
                    val rows = JSONObject(response.response).getJSONArray("ROWS")
                    for (i in 0 until rows.length()) {
                        val jo = rows.getJSONObject(i)
                        val iter = jo.keys()
                        while (iter.hasNext()) {
                            if ("getTratamientosCobro" == iter.next()) {

                                //SI EL OBJETO TIENE CP=0 o PAYLINK<>0 NO GUARDARLO
                                val ja = jo.getJSONObject("getTratamientosCobro").getJSONArray("rows")
                                for (x in 0 until ja.length()) {
                                    try {
                                        val j = ja.getJSONObject(x)
                                        if (j.getInt("CP") != 0) { //POR AHORA IGNORAMOS EL PAYLINK
                                            listaFormasDePagoTPVS.add(
                                                    FormaPagoTPV(
                                                            j.getString("IDTRAT"),
                                                            j.getString("FORMA"),
                                                            j.getString("TRATAMIENTO"),
                                                            j.getString("DISPOSITIVO")))
                                        }
                                    } catch (e: JSONException) {
                                        Log.e("GLOBAL", "Linea " + Thread.currentThread().stackTrace[2].lineNumber + ": " + e.message)
                                    }

                                }
                            }
                        }
                        data.value = listaFormasDePagoTPVS
                    }
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

    //endregion

    //region 3 ENVÍO DE COBRO AL TPV
    fun sendPayment(_total: String, _idTratamiento: String, _codeTransaction: String) {
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
        jq.agregarFuncion("setCobro", Inicio.gb.mesaActual.numero + ";" + _idTratamiento + ";" + _total + ";" + _codeTransaction)
        jq.sendQuery()
    }

    //endregion




}
