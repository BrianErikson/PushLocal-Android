package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.networkdiscovery.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import com.beariksonstudios.automatic.pushlocal.pushlocal.PLDatabase;
import com.beariksonstudios.automatic.pushlocal.pushlocal.R;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Device;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Server;

/**
 * Created by BrianErikson on 8/18/2015.
 */
public class SyncDialog extends Dialog {
    private final Device selectedDevice;
    private Context context;

    public SyncDialog(Context context, Device device) {
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
                PLDatabase db = new PLDatabase(_this.context);

                boolean success = db.insertDevice(selectedDevice);

                if (!success) // possibly already exists so try updating
                    db.updateDevice(selectedDevice);

                Server.fetch().connectNotify(selectedDevice.ipAddress);
                _this.dismiss();
            }
        });

        super.show();
    }
}
