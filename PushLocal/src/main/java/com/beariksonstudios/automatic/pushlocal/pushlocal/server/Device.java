package com.beariksonstudios.automatic.pushlocal.pushlocal.server;

/**
 * Created by BrianErikson on 9/5/15.
 */
public class Device {
    public String hostName;
    public String ipAddress;
    public boolean isSaved;
    public boolean connected;

    public Device(String hostName, String ipAddress, boolean isSaved, boolean connected) {
        this.hostName = hostName;
        this.ipAddress = ipAddress;
        this.isSaved = isSaved;
        this.connected = connected;
    }
}
