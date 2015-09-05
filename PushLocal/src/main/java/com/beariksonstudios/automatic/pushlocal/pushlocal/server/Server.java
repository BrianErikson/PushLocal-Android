package com.beariksonstudios.automatic.pushlocal.pushlocal.server;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.tcp.TcpHandler;

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
    public static String UNIT = Character.toString((char) 31);
    public static String RECORD = Character.toString((char) 30);
    public static String GROUP = Character.toString((char) 29);
    public static String FILE = Character.toString((char) 28);

    private static Server singleton;
    private static volatile boolean isRunning = false;
    private Context context;

    private DatagramSocket udpSocket;
    private Thread udpThread;
    private UdpListener udpListener;

    private Thread tcpThread;
    private TcpHandler tcpHandler;

    private ServerSocket serverSock;
    private ArrayList<DeviceListener> deviceListeners;

    private ArrayList<Device> discoveredDevices;

    public Server(Context context) {
        if (singleton != null)
            throw new IllegalStateException("Server already instantiated");

        singleton = this;
        discoveredDevices = new ArrayList<>();
        deviceListeners = new ArrayList<>();

        isRunning = true;
        this.context = context;
        try {
            udpSocket = new DatagramSocket(5566);
            udpSocket.setBroadcast(true);
            serverSock = new ServerSocket(5577);
            Log.d("PushLocal", serverSock.getInetAddress().toString());

            udpListener = new UdpListener(udpSocket);
            udpThread = new Thread(udpListener);
            udpThread.start();

            tcpHandler = new TcpHandler(serverSock);
            tcpThread = new Thread(tcpHandler);
            tcpThread.start();
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

    public synchronized void addDiscoveredDevice(Device device) {
        boolean newDevice = true;
        for (Device discoveredDevice : discoveredDevices) {
            if (discoveredDevice.hostName.contains(device.hostName))
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

    public ArrayList<Device> getDiscoveredDevices() {
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

    public void connectNotify(final String address) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    byte[] data = "connect".getBytes();
                    DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(address), 7766);
                    udpSocket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public void sendNotification(String s, Bitmap icon) {
        tcpHandler.broadcastMessageToClients("notification" + UNIT + s);
    }
}
