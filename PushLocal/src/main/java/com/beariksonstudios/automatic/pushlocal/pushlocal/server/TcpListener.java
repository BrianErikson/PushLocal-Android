package com.beariksonstudios.automatic.pushlocal.pushlocal.server;

import java.net.ServerSocket;

/**
 * Created by BrianErikson on 8/17/2015.
 */
public class TcpListener implements Runnable {
    private ServerSocket serverSock;

    public TcpListener(ServerSocket serverSock) {
        this.serverSock = serverSock;
    }

    @Override
    public void run() {

    }
}
