package com.beariksonstudios.automatic.pushlocal.pushlocal.server;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.beariksonstudios.automatic.pushlocal.pushlocal.R;
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.MainActivity;
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.NotificationListener;
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.dialog.SyncDialog;
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
    public static final String NEW_DEVICE_ACTION_STATE = "State";
    public static final String CONNECTED_DEVICE_ACTION = MainActivity.BROADCAST_PREFIX + "ConnectedDevice";
    public static final String CONFIRMED_DISCONNECT_ACTION_IPADDRESS = "Confirmed Disconnect";
    public static final String CONFIRMED_DISCONNECT_ACTION = MainActivity.BROADCAST_PREFIX + "Confirmed Disconnect";
    public static String CONNECTED_DEVICE_ACTION_IPADDRESS = "IpAddress";
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
    private static int NOTIFICATION_ID = 777;

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

            tcpHandler = new TcpHandler(serverSock, this);
            tcpThread = new Thread(tcpHandler);
            tcpThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            isRunning = false;
        }
    }

    @Override
    public void onCreate() {
        startReceiver();
        startNotification();
    }
    public void startNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Push Local Server")
                .setContentText("The Push Local server is running")
                .setOnlyAlertOnce(true)
                .setOngoing(true);

        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }


    private void startReceiver() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(MainActivity.BROADCAST_ACTION);
        iFilter.addAction(SyncDialog.CONNECT_ACTION);
        iFilter.addAction(NotificationListener.NOTIFICATION_ACTION);
        iFilter.addAction(MainActivity.REQUEST_DEVICES_ACTION);
        iFilter.addAction(SyncDialog.DISCONNECT_ACTION);
        broadcastReceiver = new BroadcastReceiver(this);
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
            intent.putExtra(NEW_DEVICE_ACTION_STATE, device.connected);
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
                    Log.d("PushLocal", "Sending connection notification to " + address);
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
        if (broadcastReceiver == null) {
            startReceiver();
        }
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
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
    public synchronized void onDeviceConnection(String hostAddress){
        Intent intent = new Intent();
        intent.putExtra(CONNECTED_DEVICE_ACTION_IPADDRESS, hostAddress);
        intent.setAction(CONNECTED_DEVICE_ACTION);
        sendBroadcast(intent);
    }

    private class BroadcastReceiver extends android.content.BroadcastReceiver {

        private final Server server;

        public BroadcastReceiver(Server server) {
            this.server = server;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MainActivity.BROADCAST_ACTION)) {
                broadcast();
            } else if (intent.getAction().equals(SyncDialog.CONNECT_ACTION)) {
                connectNotify(intent.getStringExtra(SyncDialog.CONNECT_ACTION_IP_ADDRESS));
            } else if (intent.getAction().equals(NotificationListener.NOTIFICATION_ACTION)) {
                sendNotification(intent.getStringExtra(NotificationListener.NOTIFICATION_ACTION_NOTIFICATION));
            } else if (intent.getAction().equals(MainActivity.REQUEST_DEVICES_ACTION)) {
                for (Device device : discoveredDevices) {
                    Log.e("PushLocal", device.hostName);
                    Intent requestIntent = new Intent();
                    requestIntent.putExtra(NEW_DEVICE_ACTION_HOSTNAME, device.hostName);
                    requestIntent.putExtra(NEW_DEVICE_ACTION_IP_ADDRESS, device.ipAddress);
                    requestIntent.setAction(NEW_DEVICE_ACTION);
                    server.sendBroadcast(requestIntent);
                }
            }
            else if(intent.getAction().equals(SyncDialog.DISCONNECT_ACTION)){
                String disIP = intent.getStringExtra(SyncDialog.DISCONNECT_ACTION_IP_ADDRESS);
                boolean success = tcpHandler.removeClient(disIP);
                if(success){
                    Intent requestIntent = new Intent();
                    requestIntent.putExtra(CONFIRMED_DISCONNECT_ACTION_IPADDRESS, disIP);
                    requestIntent.setAction(CONFIRMED_DISCONNECT_ACTION);
                    server.sendBroadcast(requestIntent);
                }
            }
        }
    }
}
