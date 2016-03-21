package com.beariksonstudios.automatic.pushlocal.pushlocal.server.tcp

import android.util.Log
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Server

import java.io.IOException
import java.net.InetAddress
import java.net.Socket

/**
 * Created by BrianErikson on 8/18/2015.
 */
class TcpClient(private val socket: Socket, private val tcpHandler: TcpHandler) : Thread() {
    private val PACKET_SIZE = 1024

    override fun run() {
        var data = ByteArray(PACKET_SIZE)

        while (Server.isRunning && !socket.isClosed) {
            try {
                val len = socket.inputStream.read(data)
                if (len < 0) {
                    socket.close()
                    break
                }

                var str = String(data)
                str = str.trim { it <= ' ' }
                data = ByteArray(PACKET_SIZE)

                Log.d("PushLocal", "Recieved Message from " + socket.inetAddress.hostName + ": " + str)
                handleMessage(str)
                Thread.sleep(0)
            } catch (e: IOException) {
                Log.e("PushLocal", " " + e.message)
            } catch (e: InterruptedException) {
                Log.e("PushLocal", " " + e.message)
            }
        }

        tcpHandler.removeClient(this)
    }

    @Throws(IOException::class)
    private fun handleMessage(msg: String) {
        if (msg.contains("connected")) {
            socket.outputStream.write("Indeed, we are.".toByteArray())
        }
    }

    // TcpHandler thread
    @Synchronized @Throws(IOException::class)
    fun sendMessage(msg: String) {
        socket.outputStream.write(msg.toByteArray())
    }

    val inetAddress: InetAddress
        get() = socket.inetAddress

    @Synchronized @Throws(IOException::class)
    fun dispose() {
        socket.close()
    }
}
