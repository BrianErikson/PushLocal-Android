package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.test.mock.MockContext;
import android.util.Log;
import android.widget.Toast;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Server;

/**
 * Created by nphel on 8/21/2015.
 */
public class NotificationListener extends NotificationListenerService {
    @Override
    public void onCreate() {
        super.onCreate();


        Log.d("PushLocal", "Test log onCreate");
        Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String title = sbn.getNotification().extras.getString("android.title");
        String text = sbn.getNotification().extras.getString("android.text");
        String subText = sbn.getNotification().extras.getString("android.subText");
        Bitmap largeIcon = sbn.getNotification().extras.getParcelable("android.largeIcon");
        String notification = title + Server.UNIT + text + Server.UNIT + subText;
        Log.d("PushLocal", notification);
        Toast.makeText(getApplicationContext(), "Notification Posted", Toast.LENGTH_LONG).show();
        Server.fetch().sendNotification(notification, largeIcon);
    }
}
