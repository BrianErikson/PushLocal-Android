package com.beariksonstudios.automatic.pushlocal.pushlocal.server;

import android.util.Pair;

import java.net.InetAddress;

/**
 * Created by BrianErikson on 8/17/2015.
 */
public interface DeviceListener {
    void onDeviceDiscovery(Pair<String, InetAddress> device);
}
