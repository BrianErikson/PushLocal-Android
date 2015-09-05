package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.networkdiscovery;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.beariksonstudios.automatic.pushlocal.pushlocal.R;
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.networkdiscovery.dialog.SyncDialog;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Device;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.DeviceListener;

import java.util.ArrayList;

public class NetworkDisoveryActivity extends ActionBarActivity {
    private DeviceListener deviceListener;
    private ArrayList<Device> discoveredDevices = new ArrayList<>();
    private BroadcastReceiver broadcastReceiver;
    private DiscoveredListAdapter dListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_disovery);

        final NetworkDisoveryActivity _this = this;
        ListView list = (ListView) findViewById(R.id.discovered_devices_list);
        dListAdapter = new DiscoveredListAdapter(this, R.id.discovered_devices_list,
                discoveredDevices);
        list.setAdapter(dListAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SyncDialog syncDialog = new SyncDialog(_this, discoveredDevices.get(position));
                syncDialog.show();
            }
        });


        Button button = (Button) findViewById(R.id.discovery_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent().setAction("broadcast"));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        broadcastReceiver = new BroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("NewDevice");
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_network_disovery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class BroadcastReceiver extends android.content.BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("NewDevice")) {
                discoveredDevices.add(new Device(intent.getStringExtra("HostName"), intent.getStringExtra("IpAddress")));
                dListAdapter.notifyDataSetChanged();
            }
        }
    }
}
