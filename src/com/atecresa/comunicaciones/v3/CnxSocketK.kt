package com.atecresa.comunicaciones.v3

import android.util.Log
import com.atecresa.preferencias.PreferenciasManager
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/*
NUEVA CLASE SOCKET EN KOTLIN
 */

class CnxSocketK {

    //FUNCIONES Y OBJETOS ESTÁTICOS
    companion object{
        fun send(peticion: String,listenerCnx: ListenerCnx) {
            val cnxResponse = CnxResponseK("", 0, "0")
            var res = ""
            val nintentos = 5
            var intentos = 1
            //POR AHORA NO RECIBO ESTO DESDE FUERA. YA VEREMOS EN LA IMPLEMENTACIÓN FINAL
            val ip = PreferenciasManager.getIp()
            val puerto = PreferenciasManager.getPuerto()
            val timeOut = PreferenciasManager.getTimeOut()

            while (intentos <= nintentos && res == "") {
                res = sendSocket(ip, puerto, timeOut, peticion)
                if (res == "")
                    try {
                        Thread.sleep(500)
                    } catch (e: InterruptedException) {
                        Log.e("CLIENTE SOCKET","Linea " + Thread.currentThread().stackTrace[2].lineNumber + ": " + e.message)
                        cnxResponse.error = e.toString()
                        listenerCnx.notifyError(cnxResponse)
                    }

                intentos++
            }
            cnxResponse.response = res
            if (res == "") {
                listenerCnx.notifyError(cnxResponse)
            } else {
                listenerCnx.notifySuccess(cnxResponse)
            }
        }

        private fun sendSocket(ip: String, puerto: Int, timeOutPam: Int,
                               peticionPam: String): String {
            var timeOut = timeOutPam
            var peticion = peticionPam
            val fin = "|END|"
            timeOut *= 1000
            try {
                if (!peticion.contains(fin))
                    peticion += fin
                //parche PARA 4.2.2

                val socket = java.net.Socket()
                socket.keepAlive = true //TODO controlar los tiempos de inactividad.
                socket.connect(java.net.InetSocketAddress(ip, puerto), timeOut)
                socket.soTimeout = timeOut
                val streamOut = java.io.DataOutputStream(
                        socket.getOutputStream())
                streamOut.writeBytes(peticion)
                val inputStreamReader = InputStreamReader(
                        socket.getInputStream(), StandardCharsets.ISO_8859_1) //TODO PROBAR SI VA BIEN CON ESTE CHARSET
                val bufferedReader = java.io.BufferedReader(
                        inputStreamReader, 8192)
                val t = java.util.Date().time

                val response = StringBuilder(100000)
                // String cap1 = response.capacity()+"";
                var cad = bufferedReader.readLine()
                response.append(cad)
                while (!cad.contains(fin) && java.util.Date().time - t < timeOut) {
                    cad = bufferedReader.readLine()
                    response.append(cad)//.append("\n");  //QUITAR O PONER EL \N SI ROMPE
                }

                if (java.util.Date().time - t > timeOut)
                    cad = ""

                streamOut.close()
                bufferedReader.close()
                socket.close()
                return if (cad.contains(fin))
                    response.toString().replace(fin, "")
                else
                    "" // timeout
            } catch (e: Exception) {
                return ""
            }

        }
    }




}