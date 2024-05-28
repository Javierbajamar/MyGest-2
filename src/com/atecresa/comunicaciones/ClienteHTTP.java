package com.atecresa.comunicaciones;

import android.util.Log;

import com.atecresa.preferencias.PreferenciasManager;
import com.atecresa.preferencias.Sistema;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by carlosr on 29/12/2017.
 * NUEVA CLASE CON LA IMPLEMENTACIÓN URL PARA EL CONSUMO DE SERVICIOS WEB. HACIENDO USO DEL UUID
 */

public class ClienteHTTP {

    public static String ejecutar(String json) {
        String resultado = "";

        try {
            URL url;
            url = new URL(PreferenciasManager.getUrlServidor() + json);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader r = new BufferedReader(new InputStreamReader(in, Sistema.Character_Set));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
            //TODO Hemos quitado los decode y trabajar así. Vamos a ver que tal va
            /*
            if (Preferencias.getEnvioEncriptado()) {
                resultado = Codificador.decodeJSON(Gzip.unzip(sb.toString()));
            } else {
                resultado = Codificador.decodeJSON(sb.toString());
            }
            urlConnection.disconnect();
*/
        } catch (Exception e) {
            Log.e("HTTP_Con_Url ", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
        return resultado;
    }

}
