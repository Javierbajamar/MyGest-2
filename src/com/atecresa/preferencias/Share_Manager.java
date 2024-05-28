package com.atecresa.preferencias;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.atecresa.application.Inicio;

import java.io.File;

public class Share_Manager {
    public static class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                //DEPENDIENDO DE LA ACTIVITY QUE LA LLAME
                try {
                    if (Inicio.getContext().toString().contains("Act_Preferencias")) {
                        abrirActualizador();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public static void descargarActualizador() {
        try {
            File file = new File("storage/emulated/0/Download/Mygest-release.apk");
            if (file.exists()) {
                file.delete();
            }

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(Sistema.urlAPK));
            request.setDescription("Descargando nueva versión de Servigest");
            request.setTitle("Descargando Actualizador");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Mygest-release.apk");
            DownloadManager manager = (DownloadManager) Inicio.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void abrirActualizador() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File("storage/emulated/0/Download/Mygest-release.apk")), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Inicio.getContext().startActivity(intent);
    }


}
