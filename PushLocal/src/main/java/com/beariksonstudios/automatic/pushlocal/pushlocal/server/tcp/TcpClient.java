package com.beariksonstudios.automatic.pushlocal.pushlocal.server.tcp;

import android.util.Log;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by BrianErikson on 8/18/2015.
 */
public class TcpClient extends Thread {
    private final TcpHandler tcpHandler;
    private Socket socket;
    private int PACKET_SIZE = 1024;

    public TcpClient(Socket socket, TcpHandler tcpHandler) {
        this.socket = socket;
        this.tcpHandler = tcpHandler;
    }

    @Override
    public void run() {
        byte[] data = new byte[PACKET_SIZE];

        while (Server.isRunning() && !socket.isClosed()) {
            try {
                int len = socket.getInputStream().read(data);
                if (len < 0) {
                    socket.close();
                    break;
                }

                String str = new String(data);
                str = str.trim();
                data = new byte[PACKET_SIZE];

                Log.d("PushLocal", "Recieved Message from " + socket.getInetAddress().getHostName() + ": " + str);
                handleMessage(str);
                Thread.sleep(0);
            } catch (IOException | InterruptedException e) {
                Log.e("PushLocal", " " + socket.getInetAddress().toString() + e.getMessage());
            }
        }

        tcpHandler.removeClient(this);
    }

    private void handleMessage(String msg) throws IOException {
        if (msg.contains("connected")) {
            socket.getOutputStream().write("Indeed, we are.".getBytes());
        }
    }

    // TcpHandler thread
    public synchronized void sendMessage(String msg) throws IOException {
        socket.getOutputStream().write(msg.getBytes());
    }

    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    public synchronized void dispose() throws IOException {
        socket.close();
    }
}
