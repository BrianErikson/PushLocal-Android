package com.beariksonstudios.automatic.pushlocal.pushlocal.server;

/**
 * Created by BrianErikson on 9/5/15.
 */
public class Device {
    public String hostName;
    public String ipAddress;

    public Device(String hostName, String ipAddress) {
        this.hostName = hostName;
        this.ipAddress = ipAddress;
    }
}
