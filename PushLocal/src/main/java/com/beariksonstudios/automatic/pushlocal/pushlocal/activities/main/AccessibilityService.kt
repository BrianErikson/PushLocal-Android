package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main

import android.app.Notification
import android.content.Intent
import android.os.Parcelable
import android.provider.SyncStateContract
import android.util.Log
import android.view.accessibility.AccessibilityEvent

/**
 * Created by nphel on 11/8/2015.
 */
class AccessibilityService : android.accessibilityservice.AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val eventType = event.eventType
        if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            val sourcePackageName = event.packageName as String
            val parcelable = event.parcelableData

            if (parcelable is Notification) {
                // Statusbar Notification

                //Log.e(TAG, "Notification -> notification.tickerText :: " + notification.tickerText);
                val messages = event.text
                Log.d("Push Local", messages.toString())
                Log.d("Push Local", parcelable.tickerText.toString())
            }
        } else {
            //Log.v(TAG, "Got un-handled Event");
        }
    }

    override fun onInterrupt() {

    }
}
