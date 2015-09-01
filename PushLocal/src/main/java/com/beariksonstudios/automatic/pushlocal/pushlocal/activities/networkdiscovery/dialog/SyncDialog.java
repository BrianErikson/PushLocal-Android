package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.networkdiscovery.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.beariksonstudios.automatic.pushlocal.pushlocal.Prefs;
import com.beariksonstudios.automatic.pushlocal.pushlocal.R;
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.MainActivity;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Server;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by BrianErikson on 8/18/2015.
 */
public class SyncDialog extends Dialog {
    private final Pair<String, InetAddress> selectedDevice;
    private Context context;

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

        ListView list = (ListView) view.findViewById(R.id.listView_network_dialog);
        list.setAdapter(new SyncListAdapter(context, R.id.listView_network_dialog));

        Button button = (Button) view.findViewById(R.id.button_network_dialog);

        final SyncDialog _this = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = context.getSharedPreferences(Prefs.SAVED_DEVICES_FILE, Context.MODE_PRIVATE);
                Set<String> hostNames = prefs.getStringSet(Prefs.HOSTNAME_SET, new HashSet<String>());
                Set<String> addresses = prefs.getStringSet(Prefs.ADDRESS_SET, new HashSet<String>());

                String newAddress = Arrays.toString(selectedDevice.second.getAddress());

                boolean exists = false;
                for (String address : addresses) {
                    if (address.contentEquals(newAddress)) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    SharedPreferences.Editor editor = prefs.edit();
                    hostNames.add(selectedDevice.first);
                    editor.putStringSet(Prefs.HOSTNAME_SET, hostNames);

                    addresses.add(newAddress);
                    editor.putStringSet(Prefs.ADDRESS_SET, addresses);

                    editor.apply();
                }

                Server.fetch().connectNotify(selectedDevice.second);
                _this.dismiss();
            }
        });

        super.show();
    }
}
