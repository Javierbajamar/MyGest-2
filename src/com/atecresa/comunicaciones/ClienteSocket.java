/*
 * CLASE ENCARGADA DE REALIZAR LAS PETICIONES SOCKET
 */

package com.atecresa.comunicaciones;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClienteSocket {

    private static String ejecutar_(String ip, int puerto, int timeOut,
                                    String peticion) {
        final String FIN = "|END|";
        timeOut = timeOut * 1000;
        try {
            if (!peticion.contains(FIN))
                peticion += FIN;
            //parche PARA 4.2.2

            Socket socket = new java.net.Socket();
            socket.setKeepAlive(true); //TODO controlar los tiempos de inactividad.
            socket.connect((new java.net.InetSocketAddress(ip, puerto)), timeOut);
            socket.setSoTimeout(timeOut);
            java.io.DataOutputStream streamOut = new java.io.DataOutputStream(
                    socket.getOutputStream());
            streamOut.writeBytes(peticion);
            InputStreamReader inputStreamReader = new InputStreamReader(
                    socket.getInputStream(), StandardCharsets.ISO_8859_1); //TODO PROBAR SI VA BIEN CON ESTE CHARSET
            BufferedReader bufferedReader = new java.io.BufferedReader(
                    inputStreamReader, 8192);
            long t = (new java.util.Date()).getTime();

            StringBuilder response = new StringBuilder(100000);
            // String cap1 = response.capacity()+"";
            String cad = bufferedReader.readLine();
            response.append(cad);
            while (!cad.contains(FIN)
                    && ((new java.util.Date()).getTime() - t < timeOut)) {
                cad = bufferedReader.readLine();
                response.append(cad);//.append("\n");  //QUITAR O PONER EL \N SI ROMPE
            }

            if (new java.util.Date().getTime() - t > timeOut)
                cad = "";

            streamOut.close();
            bufferedReader.close();
            socket.close();
            if (cad.contains(FIN))
                return response.toString().replace(FIN, "");
            else
                return ""; // timeout
        } catch (Exception e) {
            return "";
        }
    }

    public static String ejecutar(String ip, int puerto, int timeOut,
                                  String peticion) {
        String ret = "";
        final int nintentos = 5;
        int intentos = 1;

        while (intentos <= nintentos && ret.equals("")) {
            ret = ejecutar_(ip, puerto, timeOut, peticion);
            if (ret.equals(""))
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {

                    Log.e("CLIENTE SOCKET", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
                }
            intentos++;
        }
        if (ret.equals(""))
            ret = "";
        return ret;

    }


}
