package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.networkdiscovery;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.beariksonstudios.automatic.pushlocal.pushlocal.R;
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.MainActivity;
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.networkdiscovery.dialog.SyncDialog;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.DeviceListener;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Server;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NetworkDisoveryActivity extends ActionBarActivity {
    private Server server;
    private DeviceListener deviceListener;

    public NetworkDisoveryActivity() {
        if (Server.fetch() == null)
            this.server = new Server(MainActivity.getContext());
        else
            this.server = Server.fetch();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_disovery);

        final NetworkDisoveryActivity _this = this;
        ListView list = (ListView) findViewById(R.id.discovered_devices_list);
        final DiscoveredListAdapter dListAdapter = new DiscoveredListAdapter(this, R.id.discovered_devices_list,
                server.getDiscoveredDevices());
        list.setAdapter(dListAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SyncDialog syncDialog = new SyncDialog(_this, server.getDiscoveredDevices().get(position));
                syncDialog.show();
            }
        });

        deviceListener = new DeviceListener() {
            @Override
            public void onDeviceDiscovery(final Pair<String, InetAddress> device) {
                _this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences prefs = getSharedPreferences(MainActivity.SAVED_DEVICES_FILE, MODE_PRIVATE);

                        SharedPreferences.Editor editor = getSharedPreferences(MainActivity.SAVED_DEVICES_FILE, MODE_PRIVATE).edit();
                        Set<String> hostNames = prefs.getStringSet("hostNames", new HashSet<String>());
                        hostNames.add(device.first);
                        editor.putStringSet("hostNames", hostNames);

                        Set<String> addresses = prefs.getStringSet("addresses", new HashSet<String>());
                        addresses.add(Arrays.toString(device.second.getAddress()));
                        editor.putStringSet("addresses", addresses);

                        editor.commit();

                        dListAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        server.addDeviceListener(deviceListener);

        Button button = (Button) findViewById(R.id.discovery_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                server.broadcast();
            }
        });
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
}
