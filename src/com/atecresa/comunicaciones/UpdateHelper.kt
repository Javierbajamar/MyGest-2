package com.atecresa.comunicaciones

import android.app.Activity
import android.content.IntentSender
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

/*
CLASE ENCARGADA DE ACTUALIZAR LA APP
 */
object UpdateHelper {

    const val REQUEST_UPDATE = 1001

    fun checkUpdate(activity: Activity) {
        // Creates instance of the manager.
        val appUpdateManager = AppUpdateManagerFactory.create(activity)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // For a flexible update, use AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            activity,
                            REQUEST_UPDATE)
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }

            }
        }
    }
}
