package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import com.beariksonstudios.automatic.pushlocal.pushlocal.PLDatabase;
import com.beariksonstudios.automatic.pushlocal.pushlocal.R;
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.MainActivity;
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.MainListAdapter;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Device;

import java.util.List;

/**
 * Created by BrianErikson on 8/18/2015.
 */
public class SyncDialog extends Dialog {
    public static final String CONNECT_ACTION = MainActivity.BROADCAST_PREFIX + "Connect";
    public static final String CONNECT_ACTION_IP_ADDRESS = "IpAddress";

    private final Device selectedDevice;
    private Context context;
    private MainListAdapter listAdapter;


    public SyncDialog(Context context, Device device, MainListAdapter listAdapter) {
        super(context);
        this.context = context;
        this.listAdapter = listAdapter;
        selectedDevice = device;
    }

    @Override
    public void show() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_main_sync, null);

        setCanceledOnTouchOutside(true);
        setCancelable(true);

        super.setContentView(view);

        ListView list = (ListView) view.findViewById(R.id.listView_network_dialog);
        list.setAdapter(new SyncListAdapter(context, R.id.listView_network_dialog, this));

        Button button = (Button) view.findViewById(R.id.button_network_dialog);

        final SyncDialog _this = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(CONNECT_ACTION_IP_ADDRESS, selectedDevice.ipAddress);
                intent.setAction(CONNECT_ACTION);
                _this.context.sendBroadcast(intent);
                _this.dismiss();
            }
        });

        super.show();
    }

    public MainListAdapter getListAdapter() {
        return listAdapter;
    }

    public Device getSelectedDevice() {
        return selectedDevice;
    }
}
