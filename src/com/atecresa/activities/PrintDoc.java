package com.atecresa.activities;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.preferencias.TpvConfig;
import com.atecresa.print.DownloadTicket;
import com.atecresa.print.Print_Bluetooth;


public class PrintDoc extends AppCompatActivity {

    ActionBar actionBar;

    //region variables bluetooth
    Button mainBtn;
    //LA CONTRASEÑA DE LA IMPRESORA BLUETOOTH ES 0000 O 1234

//endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        //region actionbar
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(TpvConfig.getAppBackColor());
        actionBar.setBackgroundDrawable(colorDrawable);
        int titleId = Resources.getSystem().getIdentifier("action_bar_title",
                "id", "android");
        TextView tv = findViewById(titleId);
        tv.setTextColor(TpvConfig.getAppForecolor());
        actionBar.setTitle("Mesa "+ Inicio.gb.getMesaActual().getNumero());
        actionBar.setSubtitle("Total: "+ Inicio.gb.getMesaActual().getTotal()+" €");
        //endregion

        //region bluetooth

        mainBtn = findViewById(R.id.BTPrint);
        mainBtn.setOnClickListener(v -> new DescargarTxtTicket().execute());

        //endregion
    }

    /*
    DESCARGAR TEXTO DEL TICKET
    COMPROBAR QUE EL BLUETOOTH ESTÁ ENCENDIDO
    COMPROBAR QUE TENEMOS CONFIGURADA UNA IMPRESORA BLUETOOTH. SI NO, ENVÍAMOS  AL TPV
    CREAR BOOLEANO PARA ESTO ÚLTIMO EN LA CLASE DE PREFERENCIAS
     */

    private class DescargarTxtTicket extends AsyncTask<Void, Void, Void> {

        String txtTicket="";

        @Override
        protected Void doInBackground(Void... params) {
            txtTicket= DownloadTicket.INSTANCE.getTicket();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!txtTicket.equals("")){
                new Print_Bluetooth().print(txtTicket);
                //IntentPrint(txtTicket);
            }
        }
    }
}



