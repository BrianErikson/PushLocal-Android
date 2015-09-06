package com.beariksonstudios.automatic.pushlocal.pushlocal.server;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.MainActivity;
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.NotificationListener;
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.networkdiscovery.NetworkDisoveryActivity;
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.networkdiscovery.dialog.SyncDialog;
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
public class Server extends Service {
    public static final String NEW_DEVICE_ACTION = MainActivity.BROADCAST_PREFIX + "NewDevice";
    public static final String NEW_DEVICE_ACTION_HOSTNAME = "HostName";
    public static final String NEW_DEVICE_ACTION_IP_ADDRESS = "IpAddress";
    public static String UNIT = Character.toString((char) 31);
    public static String RECORD = Character.toString((char) 30);
    public static String GROUP = Character.toString((char) 29);
    public static String FILE = Character.toString((char) 28);

    private static volatile boolean isRunning = false;

    private DatagramSocket udpSocket;
    private Thread udpThread;
    private UdpListener udpListener;

    private Thread tcpThread;
    private TcpHandler tcpHandler;

    private ServerSocket serverSock;
    private ArrayList<DeviceListener> deviceListeners;

    private ArrayList<Device> discoveredDevices;
    private BroadcastReceiver broadcastReceiver;

    public Server() {

        discoveredDevices = new ArrayList<>();
        deviceListeners = new ArrayList<>();
        isRunning = true;

        try {
            udpSocket = new DatagramSocket(5566);
            udpSocket.setBroadcast(true);
            serverSock = new ServerSocket(5577);
            Log.d("PushLocal", serverSock.getInetAddress().toString());

            udpListener = new UdpListener(udpSocket, this);
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

    @Override
    public void onCreate() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(NetworkDisoveryActivity.BROADCAST_ACTION);
        iFilter.addAction(SyncDialog.CONNECT_ACTION);
        iFilter.addAction(NotificationListener.NOTIFICATION_ACTION);
        broadcastReceiver = new BroadcastReceiver();
        registerReceiver(broadcastReceiver, iFilter);
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
            Intent intent = new Intent();
            intent.putExtra(NEW_DEVICE_ACTION_HOSTNAME, device.hostName);
            intent.putExtra(NEW_DEVICE_ACTION_IP_ADDRESS, device.ipAddress);
            intent.setAction(NEW_DEVICE_ACTION);
            sendBroadcast(intent);
        }
    }
    public ArrayList<Device> getDiscoveredDevices() {
        return discoveredDevices;
    }

    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
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

    public void sendNotification(String s) {
        tcpHandler.broadcastMessageToClients("notification" + UNIT + s);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        try {
            udpListener.dispose();
            tcpHandler.dispose();
            udpThread.join();
            tcpThread.join();
            serverSock.close();
            unregisterReceiver(broadcastReceiver);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class BroadcastReceiver extends android.content.BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(NetworkDisoveryActivity.BROADCAST_ACTION)){
                broadcast();
            }
            else if(intent.getAction().equals(SyncDialog.CONNECT_ACTION)){
                connectNotify(intent.getStringExtra(SyncDialog.CONNECT_ACTION_IP_ADDRESS));
            }
            else if(intent.getAction().equals(NotificationListener.NOTIFICATION_ACTION)){
                sendNotification(intent.getStringExtra(NotificationListener.NOTIFICATION_ACTION_NOTIFICATION));
            }
        }
    }
}
