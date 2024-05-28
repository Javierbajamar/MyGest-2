package com.atecresa.comunicaciones.servicios;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.atecresa.activities.SelectorMesa;
import com.atecresa.application.Inicio;
import com.atecresa.preferencias.Constantes;
import com.atecresa.preferencias.PreferenciasManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServicioSocketCnx extends Service {

    public static final String BROADCAST_ACTION = "receptor"; // no se como usar
    // esto bien

    private ServerSocket serverSocket;
    private Socket client;

    final String FIN = "|END|";

    Intent intent;
    String mensaje = "";

    @Override
    public void onCreate() {
        super.onCreate();

        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServer();
        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable thread = new Runnable() {

        @Override
        public synchronized void run() {


            int timeOut = PreferenciasManager.getTimeOut() * 1000;
            while (!Thread.currentThread().isInterrupted()) { // while(true)

                try {
                    if (client != null) {
                        if (!client.isClosed())
                            client.close();
                    }
                    if (serverSocket != null) {
                        if (!serverSocket.isClosed())
                            serverSocket.close();
                    }

                } catch (Exception e) {
                    Log.e("SERVERSOCKET",
                            "Linea "
                                    + Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber() + ": "
                                    + e.getMessage());
                } finally {
                    client = null;
                    serverSocket = null;
                }

                try {
                    serverSocket = new ServerSocket(PreferenciasManager.getPuerto());
                    client = serverSocket.accept();

                    //client.setKeepAlive(true); //Volver a poner esta linea si hiciese falta
                } catch (IOException e) {
                    Log.e("SERVERSOCKET",
                            "Linea "
                                    + Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber() + ": "
                                    + e.getMessage());

                }

                try {
                    Log.i("SERVERSOCKET", "Comienza lectura");

                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(client.getInputStream(),
                                    StandardCharsets.ISO_8859_1));

                    mensaje = "";

                    long t = (new java.util.Date()).getTime();
                    Log.i("SERVERSOCKET", "String cad = input.readLine()");
                    String cad = input.readLine();

                    Log.i("SERVERSOCKET", "Stringbuilder");
                    StringBuilder response = new StringBuilder(100000);
                    response.append(cad);
                    while ((!cad.contains(FIN))
                            && ((new java.util.Date()).getTime() - t < timeOut)) {
                        Log.i("SERVERSOCKET", "readLine");
                        cad = input.readLine();
                        response.append(cad);
                    }
                    Log.i("SERVERSOCKET", "replace FIN");
                    if (response.toString().contains(FIN)) {
                        mensaje = response.toString().replace(
                                FIN, "");
                    }

                    Log.i("SERVERSOCKET",
                            "Cerramos socket y procesamos mensaje");
                    client.close();
                    input.close();

                    procesarMensaje();

                } catch (Exception e) {
                    Log.e("SERVERSOCKET",
                            "Linea "
                                    + Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber() + ": "
                                    + e.getMessage());
                }
            }

        }

    };

    private void procesarMensaje() {

        try {
            if (Inicio.gb != null&&!mensaje.equals("")) {
                    if (PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_COMANDERO)) {        //TODO BUG EN ESTA PETICIÓN. CAMBIARLA. GUARDARLAS EN MEMORIA
                        //TODO BUSCAR LA FORMA DE LEER EL OBJETO Y RESOLVER CON UN SWITCH
                        //TODO BOOLEANO PARA NO ACTUALZIAR MESAS SI ESTAMOS FACTURANDO
                        // Mensajes recibidos para el comandero
                        if ((mensaje.contains("\"getmesas\""))&& (Inicio.getPantallaActual().equals(SelectorMesa.class.getName()))) {
                            if (!PreferenciasManager.isFacturando()){
                                Inicio.gb.funCargarMesasOffline(mensaje);
                                intent.putExtra("mensaje",Constantes.mensaje_actualizacion_mesas); // MESAS
                                sendBroadcast(intent);
                            }
                        } else if (mensaje.contains("\"getmyorder\"")) {
                            Inicio.gb.guardarPedidosMyorder(mensaje);
                            intent.putExtra("mensaje",Constantes.mensaje_nuevo_pedido_myorder);
                            sendBroadcast(intent);
                        } else if (mensaje.contains("\"getmarchando\"")) {
                            Inicio.gb.guardarMensajeNotificar(new JSONObject(mensaje));
                            // Notificaci�n de nuevos platos
                            intent.putExtra("mensaje",Constantes.mensaje_notificacion_nuevos_platos);
                            sendBroadcast(intent);
                        } else if (mensaje.contains("\"getcfg\"")) {
                            JSONObject jo = new JSONObject(mensaje);
                            Inicio.gb.guardarConfigNEW(jo.getJSONObject("getcfg")
                                    .getJSONObject("resultado"));
                            intent.putExtra("mensaje",
                                    Constantes.mensaje_actualizacion_config);
                            sendBroadcast(intent);
                        }

                    } else if(PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_COCINA)) {
                        // Mensajes recibidos para cocina
                        //GUARDAR EN ALGÚN LUGAR EL NUM DE MESAS PARA DISCRIMINAR SI SE HA IDO UNA O ES UNA NUEVA
                        if (mensaje.contains("\"getcocina\"")) {
                            intent.putExtra("notificar", Inicio.gb.getCocina().cargarComandas(mensaje)); //CLAVE PARA NOTIFICAR SÓLO SI HAY ALGO NUEVO
                            intent.putExtra("mensaje",
                                    Constantes.mensaje_actualizacion_cocina);
                            sendBroadcast(intent);
                        }
                    }

            }
        } catch (Exception e) {
            Log.e("SERVERSOCKET", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
    }

    private Thread serverThread;

    private synchronized void startServer() {
        if (serverThread == null) {
            serverThread = new Thread(thread);
            serverThread.start();
            Log.i("SERVERSOCKET", "Inicio del servicio");
        }
    }

    private synchronized void stopServer() {
        try {
            if (client != null)
                client.close();
            Log.i("SERVERSOCKET", "Cierre de socket");
        } catch (IOException e) {
            Log.e("SERVERSOCKET", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
        }
        if (serverThread != null) {
            Thread t = serverThread;
            serverThread = null;
            t.interrupt();
            Log.i("SERVERSOCKET", "Servicio detenido");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (serverSocket != null) {
                    stopServer();
                    serverSocket.close();
                    Log.i("SERVERSOCKET", "Server socket detenido");
            }
        } catch (Exception e) {
            Log.e("SERVERSOCKET", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
        }

    }

}
