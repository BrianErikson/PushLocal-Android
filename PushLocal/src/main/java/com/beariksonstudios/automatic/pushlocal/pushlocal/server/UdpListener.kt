package com.beariksonstudios.automatic.pushlocal.pushlocal.server

import android.util.Log

import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * Created by BrianErikson on 8/17/2015.
 */
class UdpListener(private val udpSocket: DatagramSocket, private val server: Server) : Runnable {

    override fun run() {
        val data = ByteArray(1024)
        val packet = DatagramPacket(data, data.size)

        while (Server.isRunning) {
            try {
                udpSocket.receive(packet)
                handleMessage(String(data).substring(0, packet.length), packet.address)

                Thread.sleep(0)
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }

    private fun handleMessage(msg: String, fromAddress: InetAddress) {
        if (msg.contains("hostName")) {
            val split = msg.split(Server.UNIT.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            Log.d("PushLocal", "UDP Listener Handle Message IP: " + fromAddress.hostName)
            server.addDiscoveredDevice(Device(split[1], fromAddress.hostAddress, false, false, true))
        }
    }

    @Synchronized fun dispose() {
        udpSocket.close()
    }
}
