package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Server

/**
 * Created by nphel on 8/21/2015.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
class NotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val from = sbn.packageName
        val title = sbn.notification.extras.getString("android.title")
        val text = sbn.notification.extras.getString("android.text")
        val subText = sbn.notification.extras.getString("android.subText")
        val notification = from + Server.UNIT + title + Server.UNIT + text + Server.UNIT + subText
        Log.d("PushLocal", notification)

        val intent = Intent()
        intent.putExtra(NOTIFICATION_ACTION_NOTIFICATION, notification)
        intent.action = NOTIFICATION_ACTION
        sendBroadcast(intent)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
    }

    companion object {
        val NOTIFICATION_ACTION = MainActivity.BROADCAST_PREFIX + "Notification"
        val NOTIFICATION_ACTION_NOTIFICATION = "Notification"
    }
}
