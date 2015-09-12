package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Server;

/**
 * Created by nphel on 8/21/2015.
 */
public class NotificationListener extends NotificationListenerService {
    public static final String NOTIFICATION_ACTION = MainActivity.BROADCAST_PREFIX + "Notification";
    public static final String NOTIFICATION_ACTION_NOTIFICATION = "Notification";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String from = sbn.getPackageName();
        String title = sbn.getNotification().extras.getString("android.title");
        String text = sbn.getNotification().extras.getString("android.text");
        String subText = sbn.getNotification().extras.getString("android.subText");
        String notification = from + Server.UNIT + title + Server.UNIT + text + Server.UNIT + subText;
        Log.d("PushLocal", notification);
        Intent intent = new Intent();
        intent.putExtra(NOTIFICATION_ACTION_NOTIFICATION, notification);
        intent.setAction(NOTIFICATION_ACTION);
        sendBroadcast(intent);
    }
}
