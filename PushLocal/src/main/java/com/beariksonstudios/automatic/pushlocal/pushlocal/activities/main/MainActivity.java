package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main;

import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.beariksonstudios.automatic.pushlocal.pushlocal.PLDatabase;
import com.beariksonstudios.automatic.pushlocal.pushlocal.R;
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.dialog.SyncDialog;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Device;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Server;

import java.util.ArrayList;
import java.util.Timer;


public class MainActivity extends ActionBarActivity {
    public static final String BROADCAST_ACTION = MainActivity.BROADCAST_PREFIX + "Broadcast";
    public static final String REQUEST_DEVICES_ACTION = MainActivity.BROADCAST_PREFIX + "Request Devices";
    public static final String BROADCAST_PREFIX = "PushLocal.";
    private static Context mContext;
    public ArrayList<Device> devices = new ArrayList<>();
    private BroadcastReceiver broadcastReceiver;
    private MainListAdapter listAdapter;

    public static Context getContext() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final MainActivity _this = this;
        mContext = getApplicationContext();
        setContentView(R.layout.activity_main);

        ListView list = (ListView) findViewById(R.id.mainmenu_list);

        PLDatabase db = new PLDatabase(this);
        devices = db.getSavedDevices();
        listAdapter = new MainListAdapter(this, R.layout.item_main_list, devices);
        list.setAdapter(listAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("PushLocal", "HAS BEEN CLICKED ON LIST ITEM!!");
                SyncDialog syncDialog = new SyncDialog(_this, devices.get(position));
                syncDialog.show();
            }
        });
        Button discover = (Button) findViewById(R.id.mainmenu_discoverbuttton);
        final Timer timer = new Timer();
        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.schedule(new DiscoveryTimer(devices, _this), 5000);
                sendBroadcast(new Intent().setAction(BROADCAST_ACTION));
                Toast.makeText(_this, "Searching for Devices... :)", Toast.LENGTH_SHORT).show();
            }
        });

        Intent notificationIntent = new Intent(this, NotificationListener.class);
        startService(notificationIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        broadcastReceiver = new BroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Server.NEW_DEVICE_ACTION);
        filter.addAction(Server.CONNECTED_DEVICE_ACTION);
        registerReceiver(broadcastReceiver, filter);
        devices.clear();
        listAdapter.notifyDataSetChanged();
        Intent intent = new Intent();
        intent.setAction(REQUEST_DEVICES_ACTION);
        sendBroadcast(intent);
        ComponentName serviceName = startService(new Intent(this, Server.class));
        if (serviceName != null)
            Log.d("PushLocal", "Started or confirmed that Server service is running");
        else
            Toast.makeText(this, "ERROR: Could not start server", Toast.LENGTH_LONG).show();

        if (!isServiceEnabled())
            showEnableDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void showEnableDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Please enable NotificationMonitor access")
                .setTitle("Notification Access")
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // do nothing
                            }
                        })
                .create().show();
    }

    private boolean isServiceEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private class BroadcastReceiver extends android.content.BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Server.NEW_DEVICE_ACTION)) {
                Device device = new Device(intent.getStringExtra(Server.NEW_DEVICE_ACTION_HOSTNAME),
                        intent.getStringExtra(Server.NEW_DEVICE_ACTION_IP_ADDRESS),false ,intent.getBooleanExtra(Server.NEW_DEVICE_ACTION_STATE, false), true);
                devices.add(device);

                listAdapter.notifyDataSetChanged();
            }
            else if(intent.getAction().equals((Server.CONNECTED_DEVICE_ACTION))){
                String ipAddress = intent.getStringExtra(Server.CONNECTED_DEVICE_ACTION_IPADDRESS);
                for(Device d: devices){
                    if(d.ipAddress.equals(ipAddress)){
                        d.connected = true;
                        listAdapter.notifyDataSetChanged();
                        Toast.makeText(context, d.hostName + " is now connected Yo hommie", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
//NotificationCompat.Builder builder = new NotificationCompat.Builder(view.getContext())
//        .setSmallIcon(android.R.drawable.arrow_up_float)
//        .setContentText("This is a test notification")
//        .setContentTitle("TEST NOTIFICATION")
//        .setSubText("subText of Test");
//NotificationManager notificationManager = (NotificationManager) view.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
//notificationManager.notify(1234, builder.build());
