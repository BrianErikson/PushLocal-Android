package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.networkdiscovery.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.beariksonstudios.automatic.pushlocal.pushlocal.R;

import java.net.InetAddress;

/**
 * Created by BrianErikson on 8/18/2015.
 */
public class SyncDialog extends Dialog {
    private Context context;
    private final Pair<String, InetAddress> selectedDevice;

    public SyncDialog(Context context, Pair<String, InetAddress> device) {
        super(context);
        this.context = context;
        selectedDevice = device;
    }

    @Override
    public void show() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_network_discovery, null);

        setCanceledOnTouchOutside(true);
        setCancelable(true);

        super.setContentView(view);

        ListView list = (ListView)view.findViewById(R.id.listView_network_dialog);
        list.setAdapter(new SyncListAdapter(context, R.id.listView_network_dialog));

        Button button = (Button)view.findViewById(R.id.button_network_dialog);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Sending connect packet to " +  selectedDevice.first, Toast.LENGTH_LONG).show();
            }
        });

        super.show();
    }
}
