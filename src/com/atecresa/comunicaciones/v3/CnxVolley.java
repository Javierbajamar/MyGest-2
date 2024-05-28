package com.atecresa.comunicaciones.v3;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atecresa.gestionCobros.canarypay.ControlErrores;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*
CLASE PARA LLAMADA GET
EL PROPIO CONSTRUCTOR EJECUTA LA PETICIÓN A LA URL RECIBIDA Y DEVUELVE EL RESULTADO A TRAVÉS DEL LISTENER
LA PROPIA LIBRERÍA DE VOLLEY CREA SU HILO ASÍNCRONO
*/

public class CnxVolley {

    public static void sendGet(Context ctx, String url, String json, ListenerCnx listener) {
        CnxResponseK cnxResponse = new CnxResponseK("", 0, "0");
        try {
            RequestQueue queue = Volley.newRequestQueue(ctx.getApplicationContext());

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url + json,
                    response -> {
                        cnxResponse.setResponse(response);
                        listener.notifySuccess(cnxResponse);
                    },
                    error -> {
                        cnxResponse.setResponse(error.getMessage());
                        listener.notifyError(cnxResponse);
                    });
            queue.add(stringRequest);
        } catch (Exception e) {
            cnxResponse.setResponse(e.getMessage());
            listener.notifyError(cnxResponse);
        }
    }


    public static void sendPost(Context ctx, final String url, final JSONObject js, String token, ListenerCnx listener) {
        CnxResponseK cnxResponse = new CnxResponseK("", 0, "0");
        try {

            RequestQueue requestQueue = Volley.newRequestQueue(ctx);
            final String requestBody = js.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        cnxResponse.setResponse(response);
                        listener.notifySuccess(cnxResponse);
                    },
                    error -> {
                        cnxResponse.setResponse(error.getMessage());
                        listener.notifyError(cnxResponse);
                    }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                //SIEMPRE AÑADIMOS AL ENCABEZADO DEL POST, NUESTRO TOKEN OBTENIDO
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json");
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "token " + token);   //AUTORIZAMOS SIEMPRE CON EL TOKEN
                    return headers;
                }

                @Override
                public byte[] getBody() {
                    return requestBody.getBytes(StandardCharsets.UTF_8);
                }


                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        //RECOGEMOS EL VALOR DEL STATUS Y LO GUARDAMOS EN NUESTRO OBJETO RESPUESTA
                        cnxResponse.setStatusCode(response.statusCode);
                        try {
                            //OBTENEMOS EL JSON EN EL BODY Y LO DEVOLVEMOS
                            responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        } catch (UnsupportedEncodingException e) {
                            responseString = "";
                        }
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(Objects.requireNonNull(response)));
                }

                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError) {

                    try {
                        JSONObject je = new JSONObject(new String(volleyError.networkResponse.data,
                                HttpHeaderParser.parseCharset(volleyError.networkResponse.headers)));
                        cnxResponse.setError(ControlErrores.codeToString(je.getString("code")));
                    } catch (Exception ignored) {

                    }
                    return super.parseNetworkError(volleyError);
                }
            };


            requestQueue.add(stringRequest);
        } catch (Exception e) {
            cnxResponse.setResponse(e.getMessage());
            listener.notifyError(cnxResponse);
        }
    }
}
