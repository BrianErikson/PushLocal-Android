package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main

import android.widget.Toast
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Device

import java.util.ArrayList
import java.util.TimerTask

/**
 * Created by nphel on 9/26/2015.
 */
class DiscoveryTimer(private val devices: ArrayList<Device>, private val context: MainActivity) : TimerTask() {
    private var numberDevices: Int = 0

    init {
        numberDevices = devices.size
    }

    override fun run() {
        context.runOnUiThread {
            Toast.makeText(context, "${devices.size - numberDevices} new devices found", Toast.LENGTH_SHORT).show()
        }
    }
}
