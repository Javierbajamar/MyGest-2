package com.atecresa.gestionCobros.canarypay;

/*
ACTIVITY PARA COBROS USANDO EVOLUPAY
* IMPORTANTE PARA USAR LOS MÉTODOS GET Y POST AÑADIR A GRADLE:
 * implementation 'com.android.volley:volley:1.1.1'
 */

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.gestionCobros.tpv.ListenerCobrosTPV;
import com.atecresa.gestionCobros.tpv.ViewModelCobrosTPV;
import com.atecresa.preferencias.PreferenciasManager;
import com.atecresa.preferencias.TpvConfig;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.atecresa.util.Formateador.formatearImporteString;

//TODO Esta activity no enviará el cobro al tpv, lo enviará paymentactivity
public class CanarypayActivity extends AppCompatActivity implements ListenerCobrosTPV {

    //region DECLARACIONES

    private TextView tvInfo;
    private String status = ""; //AQUÍ GUARDAREMOS EL ESTADO ACTUAL DE LA PLATAFORMA DE PAGO
    private String token = "";   //TOKEN DE SEGURIDAD PARA CADA TRANSACCIÓN
    private String userId = "";  //NUESTRO ID COMO COMERCIO EN CANARYPAY
    private String clientId = ""; //ID DEL CLIENTE RECIBIDO POR NFC PARA PAGAR
    private String codeTransaction = "";

    private String firebaseUID = "";

    private String errorText = "";

    private final String url_post = PreferenciasManager.getServidorPago() + "/external/";
    private String url_token = PreferenciasManager.getServidorPago() + "/requests/request/getToken/";
    private final String url_checkQR = PreferenciasManager.getServidorPago() + "/external/check/";

    private final String url_login = PreferenciasManager.getServidorPago() + "/authentication/login"; //ANTIGUA EN DESAROLLO https://dev.backend.evolupay.net/api/v1/authentication/login

    private ImageView QR;
    private TextView tvTransaction;

    private String totalFactura = "";

    private CountDownTimer cd;

    //endregion

    //region CONSTANTES

    private final int oper_login = 1001;
    private final int oper_createtransaction = 1002;
    private final int oper_checkPayment = 1003;
    private final int oper_payNFC = 1004;
    private final int oper_auth_token = 1005;


    private final String status_ok = "200";
    //status_not_autorized = "401";
    //status_pending = "500";

    //NFC
    private PendingIntent nfcPendingIntent;
    private IntentFilter[] intentFiltersArray;


    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cobro_canarypay);
        totalFactura = Inicio.gb.getMesaActual().getTotal();
        TextView tvTotal = findViewById(R.id.tvImporteTotal);
        tvTotal.setText("Total: " + formatearImporteString(totalFactura.replace(",", ".")) + " €");

        TextView tvMesa = findViewById(R.id.tvPayMesa);
        tvMesa.setText("Mesa: " + Inicio.gb.getMesa());

        tvInfo = findViewById(R.id.tvINFO);

        tvTransaction = findViewById(R.id.tvTransaction);
        QR = findViewById(R.id.imgQR);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.forma_pago);
            actionBar.setSubtitle(R.string.msg_mostrar_cod_pago);
            actionBar.setDisplayShowHomeEnabled(false); //OCULTAMOS ICONO
            ColorDrawable colorDrawable = new ColorDrawable();
            colorDrawable.setColor(TpvConfig.getAppBackColor());
            actionBar.setBackgroundDrawable(colorDrawable);
        }

        View layout_principal = findViewById(R.id.card);
        layout_principal.setBackgroundColor(Color.parseColor("#757575"));
        Button btQR = findViewById(R.id.btGenQR);
        btQR.setTextColor(TpvConfig.getAppForecolor());
        btQR.setBackgroundColor(TpvConfig.getAppBackColor());
        btQR.setOnClickListener(arg0 -> {
            cancelTransaction();
            if (!userId.equals("")) {
                getToken(url_token, oper_createtransaction);
            } else {
                getToken(url_token, oper_login);
            }
        });

        //INICIAMOS IDENTIFICACIÓN. DEJAR COMENTADO POR AHORA
        //getToken(url_token, oper_login);

        //NUEVO LOGIN
        getLogin();
        //NFC
        NfcAdapter nfcAdpt = NfcAdapter.getDefaultAdapter(this);

        //TODO IMPLEMENTAMOS LA INTERFAZ EN LA PROPIA ACTIVITY
        //NUEVO MÉTODO PARA ENVIAR PAGOS AL TPV
        ViewModelCobrosTPV paymentViewModel = new ViewModelCobrosTPV(new ListenerCobrosTPV() {
            @Override
            public void notifyPaymentSuccess(boolean response) {

            }

            @Override
            public void notifyPaymentError(@NotNull String response) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            //ACTIVAMOS LA LECTURA DE NFC EN PRIMER PLANO
            setupForegroundDispatch(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        setupForegroundDispatch(false);
    }

    private void setupForegroundDispatch(boolean action) {
        try {
            NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            final Intent intent = new Intent(getApplicationContext(), this.getClass());
            final PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, 0);

            String[][] techList = new String[3][1];
            techList[0][0] = "android.nfc.tech.NdefFormatable";
            techList[1][0] = "android.nfc.tech.NfcA";
            techList[2][0] = "android.nfc.tech.Ndef";

            IntentFilter[] filters = new IntentFilter[2];
            filters[0] = new IntentFilter();
            filters[0].addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
            filters[0].addCategory(Intent.CATEGORY_DEFAULT);
            filters[1] = new IntentFilter();
            filters[1].addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
            filters[1].addCategory(Intent.CATEGORY_DEFAULT);
            if (action)
                nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, techList);
            else
                nfcAdapter.disableForegroundDispatch(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
            /*
            if (nfcAdapter != null) {
                if (!isForeground) {
                    //nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, techList);
                    isForeground = true;
                }
            }
            */

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            cancelTransaction();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            cancelTransaction();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    //region GET Y POST

    private void getLogin() {
        JSONObject js = new JSONObject();
        try {
            js.put("email", PreferenciasManager.getPagoEmail());
            js.put("phone", PreferenciasManager.getPagoPhone());
            js.put("password", PreferenciasManager.getPagoPassword());
            sendPOST(url_login, js, oper_auth_token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getToken(final String _url, final int _operation) {
        //FUNCIÓN QUE DESCARGA EL TOKEN Y LLAMA A LOS PROCEDIMIENTOS POST CON LOS NUEVOS TOKEN
        try {
            token = "";
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            StringRequest stringRequest = new StringRequest(Request.Method.GET, _url,
                    response -> {
                        //SI RECIBIMOS LA RESPUESTA CORRECTA
                        try {
                            token = new JSONObject(response).getString("token");
                            JSONObject js = new JSONObject();
                            switch (_operation) {
                                case oper_login:
                                    js.put("email", PreferenciasManager.getPagoEmail());
                                    js.put("phone", PreferenciasManager.getPagoPhone());
                                    js.put("password", PreferenciasManager.getPagoPassword());
                                    sendPOST(url_post + "login", js, oper_login);
                                    break;
                                case oper_createtransaction:
                                    codeTransaction = genCodeEvolupay();
                                    js.put("userId", userId);
                                    js.put("qr", codeTransaction);
                                    js.put("amount", totalFactura.replace(",", "."));
                                    sendPOST(url_post + "createtransaction", js, oper_createtransaction);
                                    break;
                                case oper_checkPayment:
                                    checkPayment(url_checkQR + codeTransaction);
                                    break;
                                case oper_payNFC:
                                    js.put("userId", clientId);
                                    js.put("qr", codeTransaction);
                                    js.put("amount", totalFactura.replace(",", "."));
                                    sendPOST(url_post + "pay", js, oper_payNFC);
                                    break;
                            }
                        } catch (Exception e) {
                            token = "";
                        }
                        //token = response.substring(0, 500);
                    }, error -> {
                        token = "";
                        showDialogError();
                    });
            queue.add(stringRequest);
        } catch (Exception e) {
            showDialogError();
        }
    }


    private void sendPOST(final String url, final JSONObject js, final int operation) {
        try {

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final String requestBody = js.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                Log.i("VOLLEY", response);
                switch (operation) {
                    case oper_auth_token:
                        //nuevo login
                        if (status.equals(status_ok))
                            try {
                                //auth_token = new JSONObject(response).getString("tokenAuth"); //NO ES NECESARIO
                                userId = new JSONObject(response).getJSONObject("user").getString("_id");
                                firebaseUID = new JSONObject(response).getJSONObject("user").getString("firebaseUID");
                                if (!userId.equals("")) {
                                    url_token += userId + "/" + firebaseUID;
                                    getToken(url_token, oper_createtransaction);
                                }
                            } catch (JSONException e) {
                                userId = "";
                            }
                        break;
                    case oper_createtransaction:
                        try {
                            if (new JSONObject(response).getString("status").equals("pending")) {
                                tvTransaction.setText(codeTransaction);
                                genQR(codeTransaction);
                                checkQR();
                                activeCountDown();
                            }
                        } catch (JSONException e) {
                            showDialogError();
                        }

                        break;
                    case oper_payNFC:
                        if (status.equals(status_ok)) {
                            cancelTransaction();
                            new EnviarCobro().execute(totalFactura, "0", codeTransaction); //String importe, String idTratamiento. PODEMOS ENVIAR UN 0
                        }
                        break;
                }
            }, error -> {
                cancelTransaction();
                showDialogError();
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
                    return requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8);
                }


                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        //RECOGEMOS EL VALOR DEL STATUS Y LO DEVOLVEMOS AL SUCESS
                        status = String.valueOf(response.statusCode);//LINEA ANTERIOR QUE DE
                        try {
                            //OBTENEMOS EL JSON EN EL BODY Y LO DEVOLVEMOS
                            responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        } catch (UnsupportedEncodingException e) {
                            responseString = "";
                        }
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }

                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError) {

                    try {

                        JSONObject je = new JSONObject(new String(volleyError.networkResponse.data,
                                HttpHeaderParser.parseCharset(volleyError.networkResponse.headers)));
                        errorText = ControlErrores.codeToString(je.getString("code"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return super.parseNetworkError(volleyError);
                }
            };


            requestQueue.add(stringRequest);
        } catch (Exception e) {
            showDialogError();
        }
    }

    private void checkPayment(final String _url) {
        //FUNCIÓN QUE COMPRUEBA EL ESTADO DE LOS PAGOS
        try {
            statusPago = "";
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, _url,
                    response -> {
                        try {
                            statusPago = response;
                            if (statusPago.equals(status_ok)) {
                                cancelTransaction();
                                setResult(Activity.RESULT_OK);
                                //TODO AQUÍ CERRAREMOS LA ACTIVITY CON UN OK
                                new EnviarCobro().execute(totalFactura, "0", codeTransaction); //String importe, String idTratamiento. PODEMOS ENVIAR UN 0
                            }
                        } catch (Exception e) {
                            statusPago = "";
                        }
                    }, error -> {
                        statusPago = error.networkResponse.statusCode + ""; //status: 500 Es que no ha sido pagada
                    }) {

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }


                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json");
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "token " + token);   //AUTORIZAMOS SIEMPRE CON EL TOKEN
                    return headers;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        //RECOGEMOS EL VALOR DEL STATUS Y LO SACAMOS AL SUCCESS
                        responseString = String.valueOf(response.statusCode);//LINEA ANTERIOR QUE DE
                        /* DESCARTAMOS POR AHORA EL JSON
                        try {
                            //OBTENEMOS EL JSON EN EL BODY Y LO DEVOLVEMOS
                            responseString = new String(response.items, HttpHeaderParser.parseCharset(response.headers));
                        } catch (UnsupportedEncodingException e) {
                            responseString = "";
                        }
                        */
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }


            };
            queue.add(stringRequest);

        } catch (Exception e) {
            showDialogError();
        }
    }
    //endregion

    //region GENERADOR DE QR Y TRANSACCIÓN

    /*
    FUNCIÓN CON LA QUE GENERAREMOS UN QR RECIBIENDO
    EL CONTENIDO POR PARÁMETROS
     */
    private void genQR(String contenido) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(contenido, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            QR.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String genCodeEvolupay() {
        Random r = new Random();
        String randomLetters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String res = "";
        for (int n = 0; n < 7; n++) {
            if (n == 3) //PATRÓN XXX-XXX
                res += "-";
            else
                res += randomLetters.charAt(r.nextInt(randomLetters.length()));
        }
        return res;
    }

    //endregion

    //region CHECK PAGO QR
    /*
    FUNCIÓN PARA EJECUTAR LA TAREA PROGRAMADA, POR LO MENOS DURANTE 10 O 20 SEGUNDOS,
    CONFIGURAR LA POSIBILIDAD DE DETENER LA TAREA EN CUALQUIER MOMENTO
     */


    //CUENTA ATRÁS PARA ESPERAR POR EL PAGO
    //AL FINALIZAR LA CUENTA ATRÁS SI NO SE HA PAGADO, CANCELAMOS LA TRANSACCIÓN
    private void activeCountDown() {
        //MILISEGUNDOS
        long tiempoEspera = 5 * 60000;
        cd = new CountDownTimer(tiempoEspera, 1000) {

            public void onTick(long millisUntilFinished) {
                String restTime = DateFormat.format("mm:ss", new Date(millisUntilFinished)).toString();
                tvInfo.setText("Tiempo de espera: " + restTime);
            }

            public void onFinish() {
                cancelTransaction();
                showDialogError();
            }
        }.start();
    }

    //FUNCIÓN PARA COMPROBAR EL PAGO DE LA TRANSACCIÓN ABIERTA
    private final int interval = 3000; //INTERVALO DE TIEMPO ENTRE INTENTOS. MILISEGUNDOS
    private String statusPago = "";

    private final Handler handler = new Handler();
    private final Runnable r = new Runnable() {
        public void run() {
            if (!statusPago.equals(status_ok)) { //LA CUENTA ATRÁS SERÁ EL ENCARGADO DE PARAR EL PROCESO
                getToken(url_token, oper_checkPayment);
                handler.postDelayed(r, interval); //LA TAREA SE LLAMA A SI MISMA
            }
        }
    };

    private void cancelTransaction() {

        try {
            handler.removeCallbacksAndMessages(null);
            cd.cancel(); //PARAMOS LA CUENTA ATRÁS
        } catch (Exception e) {
            e.printStackTrace();
            //ESTO FALLA EN EL API 19
        }
    }

    //LLAMADA AL RUNNABLE PARA LA EJECUCIÓN
    private void checkQR() {
        handler.postDelayed(r, interval); //HACEMOS LA LLAMADA CADA 3, CON UN TIMEOUT DE 10 SEGUNDOS
    }

    //PARAMOS CUALQUIER EJECUCIÓN EN CASO DE QUE SALGAMOS DE LA ACTIVITY
    @Override
    protected void onStop() {
        cancelTransaction();
        super.onStop();
    }

    //endregion


    //region DIALOGS


    private void showDialogOK() {
        final Dialog dialog = new Dialog(CanarypayActivity.this);
        dialog.setContentView(R.layout.dialog_cobro_canarypay);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        ImageView vCheck = dialog.findViewById(R.id.imgCheck);
        vCheck.setImageDrawable(getResources().getDrawable(R.drawable.ic_payment_ok));

        TextView tvmsg = dialog.findViewById(R.id.tv_msg_dialog_canarypay);
        tvmsg.setText("PAGO COMPLETO: " + formatearImporteString(totalFactura.replace(",", ".")) + " €");

        Button btok = dialog.findViewById(R.id.bt_dialog_firma_ok);
        btok.setOnClickListener(v -> {
            dialog.dismiss();
            Intent c = new Intent();
            c.putExtra("finalizar", true); //Este booleano por ahora no nos va a hacer falta
            setResult(Activity.RESULT_OK, c);
            finish();
        });

        Button btcancel = dialog.findViewById(R.id.bt_firma_cancelar);
        btcancel.setVisibility(View.GONE);

        dialog.show();
    }

    private void showDialogError() {
        final Dialog dialog = new Dialog(CanarypayActivity.this);
        dialog.setContentView(R.layout.dialog_cobro_canarypay);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        ImageView vCheck = dialog.findViewById(R.id.imgCheck);
        vCheck.setImageDrawable(getResources().getDrawable(R.drawable.ic_payment_error));

        TextView tvmsg = dialog.findViewById(R.id.tv_msg_dialog_canarypay);
        tvmsg.setText(errorText);

        Button btok = dialog.findViewById(R.id.bt_dialog_firma_ok);
        btok.setText("REINTENTAR");
        btok.setOnClickListener(v -> {
            dialog.dismiss();
            //getToken(url_token, oper_login);
            getLogin();
        });

        Button btcancel = dialog.findViewById(R.id.bt_firma_cancelar);
        btcancel.setOnClickListener(v -> {
            dialog.dismiss();
            Intent c = new Intent();
            c.putExtra(getString(R.string.txt_finalizar), false);
            setResult(Activity.RESULT_CANCELED, c);
            finish();
        });

        dialog.show();
    }

    //endregion

    //region NFC

    @Override
    protected void onNewIntent(Intent intent) {

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            try {
                //PARAMOS LA CONSULTA AL SERVIDOR Y PROBAMOS POR NFC
                cancelTransaction();
                tvInfo.setText("PROCESANDO PAGO NFC");
                Parcelable[] ndefMessageArray = intent.getParcelableArrayExtra(
                        NfcAdapter.EXTRA_NDEF_MESSAGES);
                NdefMessage ndefMessage = (NdefMessage) ndefMessageArray[0];
                clientId = readText(ndefMessage.getRecords()[0]);
                getToken(url_token, oper_payNFC);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onNewIntent(intent);
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        byte[] payload = record.getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        //int languageCodeLength = payload[0] & 0063; //DESCRIFRAR ESTO
        return new String(payload, 0, payload.length, textEncoding);
    }


    //endregion

    //region ENVIAR COBRO AL TPV

    private class EnviarCobro extends AsyncTask<String, Void, Void> {

        //TODO QUE HACER SI ESTA OPERACIÓN FALLA ¿?
        @Override
        protected Void doInBackground(String... pam) {
            //IMPORTE, IDTRATAMIENTO
            Inicio.gb.cargarJSONFunciones();
            Inicio.gb.desbloquearMesa(Inicio.gb.getMesa());
            Inicio.gb.enviarCobro(pam[0], pam[1], pam[2]); //TOTAL;ID_TRATAMIENTO;CODE_TRANSACTION
            Inicio.gb.ejecutarfuncion();
            return null;
        }

        @Override
        protected void onPreExecute() {
            tvInfo.setText("ENVÍANDO COBRO AL TPV"); //TODO ESTO HAY QUE PONERLO ANTES DE LA LLAMADA A LA NUEVA CLASE
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            tvInfo.setText("");
            //TODO CONTROLAR ERROR DE CONEXIÓN CON EL TPV. HAY QUE TENER EN CUENTA DE QUE EL PAGO
            //YA HA SIDO ENVIADO AL SERVIDOR. GUARDAMOS COPIA DE SEGURIDAD?
            if (Inicio.gb.sePuedeFinalizarTicket()) {
                showDialogOK();
            }
            super.onPostExecute(aVoid);
        }
    }

    //LISTENER DEL ENVIO DE COBRO AL TPV
    //TODO ESTO NO IRÁ EN ESTA CLASE. EN ESTA ACTIVITY GESTIONAREMOS
    //SÓLO LO REFERENTE A CANARYPAY Y DEVOLVEREMOS UN OK PARA QUE PAYMENTACTIVITY ENVIE EL COBRO
    @Override
    public void notifyPaymentSuccess(boolean response) {
        tvInfo.setText("");
        if (response)
            showDialogOK();
    }

    @Override
    public void notifyPaymentError(String response) {
        tvInfo.setText("");
        //TODO CASO MÁS COMPLICADO. PUEDE QUE YA HAYAMOS PAGADO POR CANARYPAY PERO NO CONECTEMOS CON EL TPV
    }

    //endregion

}
