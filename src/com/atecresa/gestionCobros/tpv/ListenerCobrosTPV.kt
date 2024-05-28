package com.atecresa.gestionCobros.tpv

interface ListenerCobrosTPV {
    //INTERFAZ PARA RECOGER LOS RESULTADOS DEL PAGO
    fun notifyPaymentSuccess(response: Boolean)  //OPERACIÓN DE PAGO CORRECTA

    fun notifyPaymentError(response: String)   //OPERACIÓN DE PAGO ERRÓNEA

}
