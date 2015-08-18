package com.beariksonstudios.automatic.pushlocal.pushlocal.server;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Pair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * Created by nphel on 8/16/2015.
 */
public class Server {
    public static String UNIT = Character.toString((char)  31);
    public static String RECORD = Character.toString((char)30);
    public static String GROUP = Character.toString((char) 29);
    public static String FILE = Character.toString((char)  28);

    private static Server singleton;
    private static volatile boolean isRunning = false;
    private Context context;

    private DatagramSocket udpSocket;
    private Thread udpThread;
    private UdpListener udpListener;

    private ServerSocket serverSock;
    private ArrayList<DeviceListener> deviceListeners;

    private ArrayList<Pair<String, InetAddress>> discoveredDevices;

    public Server(Context context) {
        if (singleton != null)
            throw new IllegalStateException("Server already instantiated");

        singleton = this;
        discoveredDevices = new ArrayList<>();
        deviceListeners = new ArrayList<>();

        isRunning = true;
        this.context = context;
        try {
            udpSocket = new DatagramSocket(7766);
            udpSocket.setBroadcast(true);
            serverSock = new ServerSocket(7777);

            udpListener = new UdpListener(udpSocket);
            udpThread = new Thread(udpListener);
            udpThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            isRunning = false;
        }
    }

    public static Server fetch() {
        return singleton;
    }

    public static boolean isRunning() {
        return isRunning;
    }

    public synchronized void addDiscoveredDevice(Pair<String, InetAddress> device) {
        boolean newDevice = true;
        for (Pair<String, InetAddress> discoveredDevice : discoveredDevices) {
            if (discoveredDevice.first.contains(device.first))
                newDevice = false;
        }

        if (newDevice) {
            discoveredDevices.add(device);
            for (DeviceListener listener : deviceListeners) {
                listener.onDeviceDiscovery(device);
            }
        }
    }

    public void addDeviceListener(DeviceListener listener) {
        deviceListeners.add(listener);
    }

    public void removeDeviceListener(DeviceListener listener) {
        deviceListeners.remove(listener);
    }

    public ArrayList<Pair<String, InetAddress>> getDiscoveredDevices() {
        return discoveredDevices;
    }

    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    public void broadcast() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    byte[] data = "broadcast".getBytes();
                    DatagramPacket packet = new DatagramPacket(data, data.length, getBroadcastAddress(), 7766);
                    udpSocket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public void connectNotify(final InetAddress address) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    byte[] data = "connect".getBytes();
                    DatagramPacket packet = new DatagramPacket(data, data.length, address, 7766);
                    udpSocket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}
