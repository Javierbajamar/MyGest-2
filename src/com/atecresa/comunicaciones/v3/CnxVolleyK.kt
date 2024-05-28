package com.atecresa.comunicaciones.v3

import android.content.Context
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.atecresa.gestionCobros.canarypay.ControlErrores
import org.json.JSONObject
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

/*
CLASE PARA LLAMADA GET
EL PROPIO CONSTRUCTOR EJECUTA LA PETICIÓN A LA URL RECIBIDA Y DEVUELVE EL RESULTADO A TRAVÉS DEL LISTENER
LA PROPIA LIBRERÍA DE VOLLEY CREA SU HILO ASÍNCRONO
//TODO CAMBIAR ESTO A CLASE, NO A OBJECT. CON LISTENER Y CONTEXT EN EL CONSTRUCTOR
*/

class CnxVolleyK {

    companion object{
        fun sendGet(ctx: Context, url: String, json: String, listener: ListenerCnx) {
            val cnxResponse = CnxResponseK("", 0, "0")
            try {
                val queue = Volley.newRequestQueue(ctx.applicationContext)

                val stringRequest = StringRequest(Request.Method.GET, url + json,
                        { response ->
                            cnxResponse.response = response
                            listener.notifySuccess(cnxResponse)
                        },
                        { error ->
                            cnxResponse.response = error.toString()
                            listener.notifyError(cnxResponse)
                        })
                queue.add(stringRequest)
            } catch (e: Exception) {
                cnxResponse.response = e.toString()
                listener.notifyError(cnxResponse)
            }

        }

        fun sendPost(ctx: Context, url: String, js: JSONObject, token: String, listener: ListenerCnx) {
            val cnxResponse = CnxResponseK("", 0, "0")
            try {

                val requestQueue = Volley.newRequestQueue(ctx)
                val requestBody = js.toString()

                val stringRequest = object : StringRequest(Method.POST, url,
                        { response ->
                            cnxResponse.response = response
                            listener.notifySuccess(cnxResponse)
                        },
                        { error ->
                            cnxResponse.response = error.toString()
                            listener.notifyError(cnxResponse)
                        }) {
                    override fun getBodyContentType(): String {
                        return "application/json; charset=utf-8"
                    }

                    //SIEMPRE AÑADIMOS AL ENCABEZADO DEL POST, NUESTRO TOKEN OBTENIDO
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Accept"] = "application/json"
                        headers["Content-Type"] = "application/json"
                        headers["Authorization"] = "token $token"   //AUTORIZAMOS SIEMPRE CON EL TOKEN
                        return headers
                    }

                    override fun getBody(): ByteArray {
                        return requestBody.toByteArray(StandardCharsets.UTF_8)
                    }


                    override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                        //RECOGEMOS EL VALOR DEL STATUS Y LO GUARDAMOS EN NUESTRO OBJETO RESPUESTA
                        cnxResponse.statusCode = response.statusCode
                        val responseString = try {
                            // OBTENEMOS EL JSON EN EL BODY Y LO DEVOLVEMOS
                            String(response.data, Charset.defaultCharset())
                        } catch (e: Exception) {
                            ""
                        }

                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response))
                    }

                    override fun parseNetworkError(volleyError: VolleyError): VolleyError {
                        volleyError.message
                        try {
                            val je = JSONObject(String(volleyError.networkResponse.data, Charset.defaultCharset()))
                            cnxResponse.error = ControlErrores.codeToString(je.getString("code"))
                        } catch (e: Exception) {

                        }

                        return super.parseNetworkError(volleyError)
                    }
                }


                requestQueue.add(stringRequest)
            } catch (e: Exception) {
                cnxResponse.response = e.toString()
                listener.notifyError(cnxResponse)
            }

        }
    }




}
