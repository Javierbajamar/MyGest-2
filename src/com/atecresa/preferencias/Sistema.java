package com.atecresa.preferencias;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import com.atecresa.application.Inicio;
import com.atecresa.login.Operador;

/**
 * Created by carlosr on 20/07/2017.
 */

public class Sistema {

    //region DATOS USUARIO
    //TODO LLENAR ESTOS CAMPOS Y QUE JSONQUERY LOS LEA DE AQUÍ

    private static Operador op;

    public static Operador getOperadorActual() {
        return op;
    }

    public static void setOperador(Operador op) {
        Sistema.op = op;
    }

    //endregion

    //VERIFICAR SI OMITIMOS LOS ELEMENTOS NUEVOS DE DISEÑO
    public static boolean usarElementosVisualesCompatibles(){
        return android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    public static String getNumeroSerie(Context ctx) {
        try {
            if (isEmulator()&&!PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_CLOUD)){
                return "123456789";
            }else if (PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_CLOUD)
                    &&PreferenciasManager.getUuid().equals("92D446EC-7CBA-4FBE-BA4C-2ACA1A3BEBD2")){
                return "DEMOMITPV";
            }else{
                return Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        } catch (Exception e) {
            return "";
        }
    }

    public static String getVersionName() {
        PackageInfo pInfo;
        try {
            pInfo = Inicio.getContext().getPackageManager().getPackageInfo(
                    Inicio.getContext().getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    public static int getVersionCode() {
        PackageInfo pInfo;
        try {
            pInfo = Inicio.getContext().getPackageManager().getPackageInfo(
                    Inicio.getContext().getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    //region EMULADOR

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    //endregion

    //region Actualizador

    public static final String urlAPK="https://www.atecresa.com/descargas/android/mygest/Mygest-release.apk";

    //endregion

    //region CARACTERES DE CODIFICACIÓN
    public static final String Character_Set = "UTF-8"; //"UTF-8"; //ISO-8859-1
    //endregion
}
