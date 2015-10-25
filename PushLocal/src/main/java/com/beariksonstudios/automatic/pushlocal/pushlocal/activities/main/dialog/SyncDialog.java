package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class SyncDialog extends DialogFragment {
    public static final String CONNECT_ACTION = MainActivity.BROADCAST_PREFIX + "Connect";
    public static final String CONNECT_ACTION_IP_ADDRESS = "IpAddress";

    private Device selectedDevice;
    private MainListAdapter listAdapter;

    public static SyncDialog newInstance(){
        return new SyncDialog();
    }

    public void initialize(Device selectedDevice, MainListAdapter listAdapter) {
        this.selectedDevice = selectedDevice;
        this.listAdapter = listAdapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_main_sync, null);
        setCancelable(true);
        ListView list = (ListView) view.findViewById(R.id.listView_network_dialog);
        list.setAdapter(new SyncListAdapter(getActivity(), R.id.listView_network_dialog, this));

        Button button = (Button) view.findViewById(R.id.button_network_dialog);

        final SyncDialog _this = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(CONNECT_ACTION_IP_ADDRESS, selectedDevice.ipAddress);
                intent.setAction(CONNECT_ACTION);
                _this.getActivity().sendBroadcast(intent);
                _this.dismiss();
            }
        });

        return view;
    }

    public MainListAdapter getListAdapter() {
        return listAdapter;
    }

    public Device getSelectedDevice() {
        return selectedDevice;
    }
}
