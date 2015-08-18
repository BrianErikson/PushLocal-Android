package com.beariksonstudios.automatic.pushlocal.pushlocal.server;

import android.util.Pair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by BrianErikson on 8/17/2015.
 */
public class UdpListener implements Runnable {
    private DatagramSocket udpSocket;

    public UdpListener(DatagramSocket udpSocket) {
        this.udpSocket = udpSocket;
    }

    @Override
    public void run() {
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, data.length);

        while (Server.isRunning()) {
            try {
                udpSocket.receive(packet);
                handleMessage(new String(data), packet.getAddress());

                Thread.sleep(0);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void handleMessage(String msg, InetAddress fromAddress) {
        if (msg.contains("hostName")) {
            String[] split = msg.split(Server.UNIT);
            Server.fetch().addDiscoveredDevice(new Pair<>(split[1], fromAddress));
        }
    }
}