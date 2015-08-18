package com.beariksonstudios.automatic.pushlocal.pushlocal.server.tcp;

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

        while (Server.isRunning()) {
            try {
                if (socket.getInputStream().available() >= 0) {
                    socket.getInputStream().read(data);
                    String str = new String(data);
                    data = new byte[PACKET_SIZE];

                    tcpHandler.addMessage(new Message(socket.getInetAddress(), str));
                    Thread.sleep(0);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    // TcpHandler thread
    public synchronized void sendMessage(String msg) throws IOException {
        socket.getOutputStream().write(msg.getBytes());
    }

    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }
}
