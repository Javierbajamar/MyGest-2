package com.atecresa.notificaciones;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.preferencias.Constantes;
import com.atecresa.preferencias.PreferenciasManager;

import java.util.Objects;

public class Notifica {

    //region ALERTAS

    public static void mostrarAlerta(Context c, String msg) {
        try {
            final AlertDialog d = new AlertDialog.Builder(c)
                    .setPositiveButton(c.getString(R.string.dialog_ok), null)
                    .setIcon(android.R.drawable.ic_dialog_alert).setMessage(msg)
                    .create();

            d.show();
        } catch (Exception e) {
            Log.e("CLASS NOTIFICA", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
    }

    //endregion

	//region DIALOGS

	static ProgressDialog pDialog;


    public static void mostrarProgreso(Context c, String msgTitulo, String msg,
                                       boolean show) {
        try {
        	//Si ya existe el dialog lo pongo a null siempre
			if (pDialog!=null){
				if (pDialog.isShowing()){
					pDialog.dismiss();
					pDialog = null;
				}
			}
			//Si hay que mostrarlo lo inicializo
            if (show) {
				pDialog = new ProgressDialog(c);
                pDialog.setMessage((msg));
                pDialog.setTitle(msgTitulo);
                pDialog.show();
            }
        } catch (Exception e) {
            Log.e("CLASS NOTIFICA", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }

    }

	//endregion

	//region TOASTS

	/*
	 * Notifica.mostrarToast(CONTEXT DEL ACTIVITY,"texto a mostrar", TIPO DE MENSAJE,
	 * DURACI�N EN PANTALLA);
	 * 
	 * Notifica.mostrarToast(Act_Comanda.this,"texto a mostrar", 2,
	 * Toast.LENGTH_SHORT);
	 * 
	 * TIPO 1 MENSAJE INFORMATIVO AZUL
	 * TIPO 2 MENSAJE DE ERROR
	 */

	public static void mostrarToast(Context c, String msg, int tipo,
			int duration) {
		try {
			LayoutInflater inflater = (LayoutInflater) c
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			AppCompatActivity activity = (c instanceof AppCompatActivity) ? (AppCompatActivity) c : null;
			View layout = null;
			switch (tipo) {
            case 1:
				assert activity != null;
				layout = inflater.inflate(R.layout.toast_custom_info,
						Objects.requireNonNull(activity).findViewById(R.id.toast_layout_root));
                break;
            case 2:
				assert activity != null;
				layout = inflater.inflate(R.layout.toast_custom_error,
						Objects.requireNonNull(activity).findViewById(R.id.toast_layout_root));
                break;
            }
			Toast toast = new Toast(c.getApplicationContext());
			assert layout != null;
			TextView txtToast = Objects.requireNonNull(layout).findViewById(R.id.txtToast);
			txtToast.setText(msg);
			toast.setGravity(Gravity.BOTTOM, 0, 0);
			toast.setDuration(duration);
			toast.setView(layout);
			toast.show();
		} catch (Exception e) {
			Log.e("CLASS NOTIFICA", "Linea "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber()
					+ ": " + e.getMessage());
		}
	}

    public static void mostrarToastSimple(Context c, String msg) {
        try {
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(c, msg, duration);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL,
                    0, 0);
            toast.show();
        } catch (Exception e) {
            Log.e("CLASS NOTIFICA", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
    }

	public static void mostrarMensajeEstado(Context c) {
		try {
			switch (Inicio.gb.getEstadoApp()) {
            case Constantes.app_demo_caducada:
                mostrarToast(
                        c,
                        c.getResources().getString(
                                (R.string.mensaje_error_caducado)), 2,
                        Toast.LENGTH_LONG);
                break;
            case Constantes.app_serie_no_registrado:
                mostrarAlerta(
                        c,
                        c.getResources().getString(
                                (R.string.mensaje_error_licencia))
                                + " " + Inicio.gb.getNumeroSerie());
                break;
            case Constantes.app_serie_registrado:
                if (Inicio.gb.getError() == Constantes.error_insertando_lineas) {
                    mostrarToast(
                            c,
                            c.getResources().getString(
                                    R.string.mensaje_lineas_no_insertadas), 2,
                            Toast.LENGTH_SHORT);
                }
                break;
            }
		} catch (Resources.NotFoundException e) {
			Log.e("CLASS NOTIFICA", "Linea "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber()
					+ ": " + e.getMessage());
		}

	}

    //endregion



	public static void notificar(Context c, String ticker, String titulo,
			String mensaje) {

		try {
			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					c.getApplicationContext())
					.setSmallIcon(R.drawable.ic_launcher).setTicker(ticker)
					.setContentTitle(titulo).setContentText(mensaje)
					.setAutoCancel(true).setOnlyAlertOnce(false);

			if (PreferenciasManager.notificarSonido()) {
				builder.setDefaults(Notification.DEFAULT_SOUND
						| Notification.DEFAULT_VIBRATE
						| Notification.DEFAULT_LIGHTS);
			} else {
				builder.setDefaults(Notification.DEFAULT_VIBRATE
						| Notification.DEFAULT_LIGHTS);
			}

			PendingIntent contentIntent;
			Intent notificationIntent = new Intent(); // ESTO SE SUPONE QUE NO
														// HACE NADA
			contentIntent = PendingIntent.getActivity(
					c.getApplicationContext(), 0, notificationIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(contentIntent);
			// Add as notification
			NotificationManager manager = (NotificationManager) c
					.getSystemService(Context.NOTIFICATION_SERVICE);
			manager.notify(1, builder.build());
		} catch (Exception e) {
			Log.e("CLASS NOTIFICA", "Linea "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber()
					+ ": " + e.getMessage());
		}

	}


}
