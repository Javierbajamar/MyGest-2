package com.atecresa.print

import com.atecresa.application.Inicio
import com.atecresa.comunicaciones.ClienteHTTP
import com.atecresa.comunicaciones.ClienteSocket
import com.atecresa.preferencias.PreferenciasManager
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by carlosr on 19/07/2017.
 * CLASE PARA OBTENER LA SECUENCIA DE ESCAPE PARA IMPRIMIR UN TICKET
 * HAY QUE LLAMARLA DESDE CLASE ASÍNCRONA
 */

object DownloadTicket {

    //region 1 GET TICKET
    //ID Y ; 1 .> POS
    //    CP437
    val ticket: String
        get() {
            var res = ""
            try {
                val peticion = JSONObject(
                        "{\"FUNCIONES\":[{\"setserie\":\"" + Inicio.getNserie()
                                + "\"},{\"setoperador\":\"" + Inicio.gb.operadorActual.id + ";"
                                + Inicio.gb.operadorActual.clave + "\"}]}")
                val funcion = JSONObject()
                funcion.put("getTicket", Inicio.gb.mesaActual.id + ";" + "1")
                peticion.getJSONArray("FUNCIONES").put(funcion)

                val filas = JSONObject(ClienteSocket.ejecutar(
                        PreferenciasManager.getIp(), PreferenciasManager.getPuerto(), PreferenciasManager.getTimeOut(),
                        "$peticion|END|"))
                        .getJSONArray("ROWS")

                for (i in 0 until filas.length()) {
                    val obj = filas.getJSONObject(i)
                    if (obj.has("getTicket")) {
                        res = obj.getJSONObject("getTicket").getString("resultado")
                    }

                }

                val st = res.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val b = ByteArray(st.size)
                for (i in st.indices) {
                    val bb = Integer.parseInt(st[i])
                    when (bb) {

                        164 -> b[i] = "ñ".toByteArray()[0]
                        165 -> b[i] = "Ñ".toByteArray()[0]
                        160 -> b[i] = "á".toByteArray()[0]
                        130 -> b[i] = "é".toByteArray()[0]
                        161 -> b[i] = "í".toByteArray()[0]
                        162 -> b[i] = "ó".toByteArray()[0]
                        163 -> b[i] = "ú".toByteArray()[0]
                        238 -> b[i] = "€".toByteArray()[0]
                        144 -> b[i] = "É".toByteArray()[0]

                        else -> b[i] = bb.toByte()
                    }
                }
                res = String(b)

                return String((res + "\n\n\n").toByteArray(charset("CP437")))
            } catch (e: Exception) {
                return ""
            }

        }

    //endregion

    //region 2 GET X
    //TODO Aquí el string viene bien, se va todo al carajo con esta conversión
    //return new String((res + "\n\n\n").getBytes("CP437")); //    CP437
    val x: String
        get() {
            var res = ""
            try {
                val peticion = JSONObject()
                peticion.put("UUID", PreferenciasManager.getUuid())
                val ja = JSONArray()
                ja.put(JSONObject().put("setserie", Inicio.gb.numeroSerie))
                ja.put(JSONObject().put("getX", ""))
                peticion.put("FUNCIONES", ja)
                val filas = JSONObject(ClienteHTTP.ejecutar(peticion.toString())).getJSONArray("ROWS")

                for (i in 0 until filas.length()) {
                    val obj = filas.getJSONObject(i)
                    if (obj.has("getX")) {
                        res = obj.getJSONObject("getX").getString("resultado")
                    }

                }
                res += "\n\n\n"
                return res
            } catch (e: Exception) {
                return ""
            }

        }

    //endregion

    //region 3 GET Z

    private fun getZ(id: String): String {
        var res = ""
        try {
            val peticion = JSONObject()
            peticion.put("UUID", PreferenciasManager.getUuid())
            val ja = JSONArray()
            ja.put(JSONObject().put("setserie", Inicio.gb.numeroSerie))
            ja.put(JSONObject().put("getZ", id))
            peticion.put("FUNCIONES", ja)
            val filas = JSONObject(ClienteHTTP.ejecutar(peticion.toString())).getJSONArray("ROWS")

            for (i in 0 until filas.length()) {
                val obj = filas.getJSONObject(i)
                if (obj.has("getZ")) {
                    res = obj.getJSONObject("getZ").getString("resultado")
                }

            }
            return res + "\n\n\n"
        } catch (e: Exception) {
            return ""
        }

    }

    fun setZ(): String {
        var res = ""
        try {
            val peticion = JSONObject()
            peticion.put("UUID", PreferenciasManager.getUuid())
            val ja = JSONArray()
            ja.put(JSONObject().put("setserie", Inicio.gb.numeroSerie))
            ja.put(JSONObject().put("setZ", ""))
            peticion.put("FUNCIONES", ja)
            val filas = JSONObject(ClienteHTTP.ejecutar(peticion.toString())).getJSONArray("ROWS")

            for (i in 0 until filas.length()) {
                val obj = filas.getJSONObject(i)
                if (obj.has("setZ")) {
                    res = obj.getJSONObject("setZ").getString("resultado")
                }
            }
            return getZ(res)
        } catch (e: Exception) {
            return ""
        }

    }

    //endregion

    //region 4 UTILIDADES
    private fun replaceTXT(vText: String): String {
        var vText = vText
        vText = vText.replace("" + 164.toChar(), "ñ")
        vText = vText.replace("" + 165.toChar(), "Ñ")
        vText = vText.replace("" + 160.toChar(), "á")
        vText = vText.replace("" + 130.toChar(), "é")
        vText = vText.replace("" + 161.toChar(), "í")
        vText = vText.replace("" + 162.toChar(), "ó")
        vText = vText.replace("" + 163.toChar(), "ú")
        vText = vText.replace("" + 238.toChar(), "€")
        vText = vText.replace("Á", "A")
        vText = vText.replace("" + 144.toChar(), "É")
        vText = vText.replace("Í", "I")
        vText = vText.replace("Ó", "O")
        vText = vText.replace("Ú", "U")
        return vText
    }

    //endregion
}
