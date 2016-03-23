package com.beariksonstudios.automatic.pushlocal.pushlocal.server

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.beariksonstudios.automatic.pushlocal.pushlocal.R
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.MainActivity
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.NotificationListener
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.dialog.SyncDialog
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.tcp.TcpHandler

import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.ServerSocket
import java.util.ArrayList

/**
 * Created by nphel on 8/16/2015.
 */
class Server : Service() {

    private var udpSocket: DatagramSocket? = null
    private var udpThread: Thread? = null
    private var udpListener: UdpListener? = null

    private var tcpThread: Thread? = null
    private var tcpHandler: TcpHandler? = null

    private var serverSock: ServerSocket? = null
    private val deviceListeners: ArrayList<DeviceListener>

    val discoveredDevices: ArrayList<Device>
    private var broadcastReceiver: BroadcastReceiver? = null

    init {

        discoveredDevices = ArrayList<Device>()
        deviceListeners = ArrayList<DeviceListener>()
        isRunning = true

        try {
            udpSocket = DatagramSocket(5566)
            udpSocket!!.broadcast = true
            serverSock = ServerSocket(5577)
            Log.d("PushLocal", serverSock!!.inetAddress.toString())

            udpListener = UdpListener(udpSocket as DatagramSocket, this)
            udpThread = Thread(udpListener)
            udpThread!!.start()

            tcpHandler = TcpHandler(serverSock as ServerSocket, this)
            tcpThread = Thread(tcpHandler)
            tcpThread!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
            isRunning = false
        }

    }

    override fun onCreate() {
        startReceiver()
        startNotification()
    }

    fun startNotification() {
        val builder = NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher).setContentTitle("Push Local Server").setContentText("The Push Local server is running").setOnlyAlertOnce(true).setOngoing(true)

        val activityIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }


    private fun startReceiver() {
        val iFilter = IntentFilter()
        iFilter.addAction(MainActivity.BROADCAST_ACTION)
        iFilter.addAction(SyncDialog.CONNECT_ACTION)
        iFilter.addAction(NotificationListener.NOTIFICATION_ACTION)
        iFilter.addAction(MainActivity.REQUEST_DEVICES_ACTION)
        iFilter.addAction(SyncDialog.DISCONNECT_ACTION)
        broadcastReceiver = BroadcastReceiver(this)
        registerReceiver(broadcastReceiver, iFilter)
    }

    @Synchronized fun addDiscoveredDevice(device: Device) {
        var newDevice = true
        for (discoveredDevice in discoveredDevices) {
            if (discoveredDevice.hostName.contains(device.hostName))
                newDevice = false
        }

        if (newDevice) {
            discoveredDevices.add(device)
            val intent = Intent()
            intent.putExtra(NEW_DEVICE_ACTION_HOSTNAME, device.hostName)
            intent.putExtra(NEW_DEVICE_ACTION_IP_ADDRESS, device.ipAddress)
            intent.putExtra(NEW_DEVICE_ACTION_STATE, device.connected)
            intent.action = NEW_DEVICE_ACTION
            sendBroadcast(intent)
        }
    }

    internal // handle null somehow
    val broadcastAddress: InetAddress
        @Throws(IOException::class)
        get() {
            val wifi = getSystemService(Context.WIFI_SERVICE) as WifiManager
            val dhcp = wifi.dhcpInfo

            val broadcast = dhcp.ipAddress and dhcp.netmask or dhcp.netmask.inv()
            val quads = ByteArray(4)
            for (k in 0..3)
                quads[k] = (broadcast shr k * 8 and 0xFF).toByte()
            return InetAddress.getByAddress(quads)
        }

    fun broadcast() {
        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void): Void? {
                try {
                    val data = "broadcast".toByteArray()
                    val packet = DatagramPacket(data, data.size, broadcastAddress, 7766)
                    udpSocket!!.send(packet)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                return null
            }
        }.execute()
    }

    fun connectNotify(address: String) {
        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void): Void? {
                try {
                    val data = "connect".toByteArray()
                    Log.d("PushLocal", "Sending connection notification to " + address)
                    val packet = DatagramPacket(data, data.size, InetAddress.getByName(address), 7766)
                    udpSocket!!.send(packet)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                return null
            }
        }.execute()
    }

    fun sendNotification(s: String) {
        tcpHandler!!.broadcastMessageToClients("notification" + UNIT + s)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (broadcastReceiver == null) {
            startReceiver()
        }
        return Service.START_STICKY
    }

    override fun onDestroy() {
        isRunning = false
        try {
            udpListener!!.dispose()
            tcpHandler!!.dispose()
            udpThread!!.join()
            tcpThread!!.join()
            serverSock!!.close()
            unregisterReceiver(broadcastReceiver)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    @Synchronized fun onDeviceConnection(hostAddress: String) {
        val intent = Intent()
        intent.putExtra(CONNECTED_DEVICE_ACTION_IPADDRESS, hostAddress)
        intent.action = CONNECTED_DEVICE_ACTION
        sendBroadcast(intent)
    }

    private inner class BroadcastReceiver(private val server: Server) : android.content.BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == MainActivity.BROADCAST_ACTION) {
                broadcast()
            } else if (intent.action == SyncDialog.CONNECT_ACTION) {
                connectNotify(intent.getStringExtra(SyncDialog.CONNECT_ACTION_IP_ADDRESS))
            } else if (intent.action == NotificationListener.NOTIFICATION_ACTION) {
                sendNotification(intent.getStringExtra(NotificationListener.NOTIFICATION_ACTION_NOTIFICATION))
            } else if (intent.action == MainActivity.REQUEST_DEVICES_ACTION) {
                for (device in discoveredDevices) {
                    Log.e("PushLocal", device.hostName)
                    val requestIntent = Intent()
                    requestIntent.putExtra(NEW_DEVICE_ACTION_HOSTNAME, device.hostName)
                    requestIntent.putExtra(NEW_DEVICE_ACTION_IP_ADDRESS, device.ipAddress)
                    requestIntent.action = NEW_DEVICE_ACTION
                    server.sendBroadcast(requestIntent)
                }
            } else if (intent.action == SyncDialog.DISCONNECT_ACTION) {
                val disIP = intent.getStringExtra(SyncDialog.DISCONNECT_ACTION_IP_ADDRESS)
                val success = tcpHandler!!.removeClient(disIP)
                if (success) {
                    val requestIntent = Intent()
                    requestIntent.putExtra(CONFIRMED_DISCONNECT_ACTION_IPADDRESS, disIP)
                    requestIntent.action = CONFIRMED_DISCONNECT_ACTION
                    server.sendBroadcast(requestIntent)
                }
            }
        }
    }

    companion object {
        val NEW_DEVICE_ACTION = MainActivity.BROADCAST_PREFIX + "NewDevice"
        val NEW_DEVICE_ACTION_HOSTNAME = "HostName"
        val NEW_DEVICE_ACTION_IP_ADDRESS = "IpAddress"
        val NEW_DEVICE_ACTION_STATE = "State"
        val CONNECTED_DEVICE_ACTION = MainActivity.BROADCAST_PREFIX + "ConnectedDevice"
        val CONFIRMED_DISCONNECT_ACTION_IPADDRESS = "Confirmed Disconnect"
        val CONFIRMED_DISCONNECT_ACTION = MainActivity.BROADCAST_PREFIX + "Confirmed Disconnect"
        var CONNECTED_DEVICE_ACTION_IPADDRESS = "IpAddress"
        var UNIT = Character.toString(31.toChar())
        var RECORD = Character.toString(30.toChar())
        var GROUP = Character.toString(29.toChar())
        var FILE = Character.toString(28.toChar())

        @Volatile var isRunning = false
            private set
        private val NOTIFICATION_ID = 777
    }
}
