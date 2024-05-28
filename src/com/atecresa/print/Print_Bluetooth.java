package com.atecresa.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import com.atecresa.preferencias.PreferenciasManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

/**
 * Created by carlosr on 20/07/2017.
 * esta clase imprime a través de la impresora bluetooth configurada
 */

@SuppressWarnings("ALL")
public class Print_Bluetooth {

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket socket;
    BluetoothDevice bluetoothDevice;
    OutputStream outputStream;
    InputStream inputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    public Print_Bluetooth() {
    }

    //INICIALIZA LA IMPRESORA
    private void InitPrinter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            /*
            if (!bluetoothAdapter.isEnabled()) {
                //ESTO TIENE QUE IR EN UN ACTIVITY SI O SI. ACTIVA EL BLUETOOTH. LO LLAMAMOS DESDE LA ACTIVITY ANTES DE LLAMAR A ESTA CLASE
                //Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //Inicio.getContext().startActivityForResult(enableBluetooth, 0);
            }

             */

            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals(PreferenciasManager.getImpresoraBluetooth())) //BUSCAMOS LA IMPRESORA POR DEFECTO CONFIGURADA
                    {
                        bluetoothDevice = device;
                        break;
                    }
                }

                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
                Method m = bluetoothDevice.getClass().getMethod("createRfcommSocket", int.class);
                socket = (BluetoothSocket) m.invoke(bluetoothDevice, 1);
                bluetoothAdapter.cancelDiscovery();
                socket.connect();
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                beginListenForData();
            } else {
                Log.e("Impresora bluetooth", "No hay ninguna impresora bluetooth configurada");
            }
        } catch (Exception ex) {
            Log.e("Impresora bluetooth", ex.getMessage());
        }
    }

    //ENVÍA TEXTO A LA IMPRESORA
    private void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(() -> {

                while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                    try {

                        int bytesAvailable = inputStream.available();

                        if (bytesAvailable > 0) {

                            byte[] packetBytes = new byte[bytesAvailable];
                            inputStream.read(packetBytes);

                            for (int i = 0; i < bytesAvailable; i++) {

                                byte b = packetBytes[i];
                                if (b == delimiter) {

                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(
                                            readBuffer, 0,
                                            encodedBytes, 0,
                                            encodedBytes.length
                                    );

                                    // specify US-ASCII encoding
                                    final String data = new String(encodedBytes, StandardCharsets.US_ASCII);
                                    readBufferPosition = 0;

                                    // tell the user items were sent to bluetooth printer device
                                    handler.post(() -> Log.d("e", data));

                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }

                    } catch (Exception ex) {
                        stopWorker = true;
                    }

                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //MÉTODO PÚBLICO PARA IMPRIMIR
    public boolean print(String txtvalue) {
        //txtvalue = Formateador.formatASCII(txtvalue);

        byte[] buffer = txtvalue.getBytes();
        byte[] PrintHeader = {(byte) 0xAA, 0x55, 2, 0};
        PrintHeader[3] = (byte) buffer.length;
        InitPrinter();
        if (PrintHeader.length > 128) {
            Log.e("Impresora bluetooth", "Value is more than 128 size");
            return false;
        } else {
            try {
                //LINEA ORIGINAL
                outputStream.write(txtvalue.getBytes());
                outputStream.close();
                socket.close();
                return true;
            } catch (Exception ex) {
                Log.e("Impresora bluetooth", "Excep IntentPrint");
                return false;
            }
        }
    }


}
