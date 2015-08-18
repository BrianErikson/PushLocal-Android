package com.beariksonstudios.automatic.pushlocal.pushlocal.server.tcp;

import java.net.InetAddress;

/**
 * Created by BrianErikson on 8/18/2015.
 */
public class Message {
    public InetAddress from;
    public String text;

    public Message(InetAddress from, String text) {
        this.from = from;
        this.text = text;
    }
}
