package com.beariksonstudios.automatic.pushlocal.pushlocal.server.tcp

import android.util.Log
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Server

import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.ArrayList

/**
 * Created by BrianErikson on 8/17/2015.
 */
class TcpHandler(private val serverSock: ServerSocket, private val server: Server) : Runnable {
    private val clients: ArrayList<TcpClient>

    init {
        this.clients = ArrayList<TcpClient>()
    }

    override fun run() {
        while (Server.isRunning) {
            try {
                val newSocket = serverSock.accept()
                addClient(newSocket)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // this thread
    fun addClient(connection: Socket) {
        var exists = false
        for (client in clients) {
            if (client.inetAddress === connection.inetAddress) {
                exists = true
                break
            }
        }

        if (!exists) {
            Log.v("PushLocal", "Adding client  " + connection.inetAddress.hostName)
            val tcpClient = TcpClient(connection, this)
            tcpClient.start()
            server.onDeviceConnection(connection.inetAddress.hostAddress)
            clients.add(tcpClient)
        }
    }

    // Server/Main thread
    @Synchronized fun removeClient(ipAddress: String): Boolean {
        var toRemove: TcpClient? = null
        for (client in clients) {
            if (client.inetAddress.hostName == ipAddress) {
                toRemove = client
                break
            }
        }
        if (toRemove != null) {
            try {
                toRemove.dispose()
            } catch (e: IOException) {
            }

            clients.remove(toRemove)
            return true
        }
        return false
    }

    // Server/Main thread
    @Synchronized fun broadcastMessageToClients(message: String) {
        for (client in clients) {
            try {
                client.sendMessage(message)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    // TcpClient(s) thread
    @Synchronized fun removeClient(client: TcpClient) {
        clients.remove(client)
        Log.e("PushLocal", "Removing disconnected client")
    }

    @Synchronized @Throws(IOException::class)
    fun dispose() {
        for (client in clients) {
            client.dispose()
        }
    }
}
