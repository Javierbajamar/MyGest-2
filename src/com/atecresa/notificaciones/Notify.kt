package com.atecresa.notificaciones

/*
Clase en Kotlin para notificaciones PUSH en Android 8
 */

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.atecresa.application.R

class Notify (mContext: Context){

    //todo Probar si esta primera función va bien con apis anteriores a O

    private val ctx=mContext //SI NO LO PONEMOS PRIVATE, DESDE FUERA SE GENERA UN GETTER AUTOMÁTICAMENTE

    fun showPushNotification(titulo: String, mensaje: String) {
        val resultIntent = Intent()
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val resultPendingIntent = PendingIntent.getActivity(ctx,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_CANCEL_CURRENT) //A VER SI ASÍ CANCELA LA LLAMADA
        val mNotificationManager: NotificationManager?
        val channelId = "10001"
        val mBuilder = NotificationCompat.Builder(ctx,channelId)
        mBuilder.setSmallIcon(R.drawable.ic_launcher_2019)
        mBuilder.setContentTitle(titulo)
                .setContentText(mensaje)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent)
        mNotificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, "NOTIFICATION_CHANNEL_NAME", importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mBuilder.setChannelId(channelId)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build())
    }

}