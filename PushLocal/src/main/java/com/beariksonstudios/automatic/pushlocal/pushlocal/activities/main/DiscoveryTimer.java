package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main;

import android.content.Context;
import android.widget.Toast;
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.MainActivity;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Device;

import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Created by nphel on 9/26/2015.
 */
public class DiscoveryTimer extends TimerTask{

    private final ArrayList<Device> devices;
    private final MainActivity context;
    int numberDevices;

    public DiscoveryTimer(ArrayList<Device> devices, MainActivity context){
        this.devices = devices;
        this.context = context;
        numberDevices = devices.size();
    }
    @Override
    public void run() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, devices.size() - numberDevices + " new devices found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
