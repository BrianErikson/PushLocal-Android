package com.beariksonstudios.automatic.pushlocal.pushlocal.server.tcp;

import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by BrianErikson on 8/17/2015.
 */
public class TcpHandler implements Runnable {
    private ServerSocket serverSock;
    private ArrayList<TcpClient> clients;
    private ArrayList<Message> messages;

    public TcpHandler(ServerSocket serverSock) {
        this.serverSock = serverSock;
        this.messages = new ArrayList<>();
    }

    @Override
    public void run() {
        while (Server.isRunning()) {
            while (Server.isRunning()) {
                try {
                    Socket newSocket = serverSock.accept();
                    addClient(newSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            System.out.println("Adding client from " + connection.getInetAddress().getHostName());
            TcpClient tcpClient = new TcpClient(connection, this);
            tcpClient.start();
            clients.add(tcpClient);
        }
    }

    // client threads
    public synchronized void addMessage(Message message) {
        messages.add(message);
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
}
