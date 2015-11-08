package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main;

import android.app.Notification;
import android.content.Intent;
import android.os.Parcelable;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.util.List;

/**
 * Created by nphel on 11/8/2015.
 */
public class AccessibilityService extends android.accessibilityservice.AccessibilityService{
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            final String sourcePackageName = (String)event.getPackageName();
            Parcelable parcelable = event.getParcelableData();

            if (parcelable instanceof Notification) {
                // Statusbar Notification

                Notification notification = (Notification) parcelable;
                //Log.e(TAG, "Notification -> notification.tickerText :: " + notification.tickerText);
                List<CharSequence> messages = event.getText();
                Log.d("Push Local", messages.toString());
                Log.d("Push Local", notification.tickerText.toString());
            }
        } else {
            //Log.v(TAG, "Got un-handled Event");
        }
    }

    @Override
    public void onInterrupt() {

    }
}
