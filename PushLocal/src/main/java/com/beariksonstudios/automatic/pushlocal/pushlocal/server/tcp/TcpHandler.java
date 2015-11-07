package com.beariksonstudios.automatic.pushlocal.pushlocal.server.tcp;

import android.util.Log;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by BrianErikson on 8/17/2015.
 */
public class TcpHandler implements Runnable {
    private final Server server;
    private ServerSocket serverSock;
    private ArrayList<TcpClient> clients;

    public TcpHandler(ServerSocket serverSock, Server server) {
        this.serverSock = serverSock;
        this.clients = new ArrayList<>();
        this.server = server;
    }

    @Override
    public void run() {
        while (Server.isRunning()) {
            try {
                Socket newSocket = serverSock.accept();
                addClient(newSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // this thread
    public void addClient(Socket connection) {
        boolean exists = false;
        for (TcpClient client : clients) {
            if (client.getInetAddress() == connection.getInetAddress()) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            Log.v("PushLocal", "Adding client  " + connection.getInetAddress().getHostName());
            TcpClient tcpClient = new TcpClient(connection, this);
            tcpClient.start();
            server.onDeviceConnection(connection.getInetAddress().getHostAddress());
            clients.add(tcpClient);
        }
    }

    // Server/Main thread
    public synchronized boolean removeClient(String ipAddress){
        TcpClient toRemove = null;
        for (TcpClient client : clients) {
            if(client.getInetAddress().getHostName().equals(ipAddress)){
                toRemove = client;
                break;
            }
        }
        if (toRemove != null) {
            try {
                toRemove.dispose();
            } catch (IOException e) {
            }
            clients.remove(toRemove);
            return true;
        }
        return false;
    }

    // Server/Main thread
    public synchronized void broadcastMessageToClients(String message) {
        for (TcpClient client : clients) {
            try {
                client.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // TcpClient(s) thread
    public synchronized void removeClient(TcpClient client) {
        clients.remove(client);
        Log.e("PushLocal", "Removing disconnected client");
    }

    public synchronized void dispose() throws IOException {
        for (TcpClient client : clients) {
            client.dispose();
        }
    }
}
