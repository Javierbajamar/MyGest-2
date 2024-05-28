package com.atecresa.gestionCobros.canarypay

import android.content.Context
import androidx.lifecycle.ViewModel
import com.atecresa.gestionCobros.tpv.ListenerCobrosTPV
import com.atecresa.preferencias.PreferenciasManager

class CanarypayViewModel(ctx:Context,pl: ListenerCobrosTPV) : ViewModel() {
    //LISTENER
    private val listenerCobrosTPV: ListenerCobrosTPV = pl
    //CONTEXT
    private val context:Context=ctx
    //URLS
    private val urlPost = """${PreferenciasManager.getServidorPago()} + "/external/"""
    private val urlToken = """${PreferenciasManager.getServidorPago()} + "/requests/request/getToken/"""
    private val urlCheckQR = """${PreferenciasManager.getServidorPago()}/external/check/"""
    private val urlLogin = """${PreferenciasManager.getServidorPago()} + "/authentication/login"""

    init {
        login()
    }

    private fun login() {
        //VOLLEY SEND POST
    }

    /*
    TODO Clase que gestionará las funciones a través de la conexión volley
    Aprovecharemos la interfaz PaymentListener para devolver los resultados de las transacciones
     */


}