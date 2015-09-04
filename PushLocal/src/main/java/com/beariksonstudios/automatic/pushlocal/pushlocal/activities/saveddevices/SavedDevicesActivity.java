package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.saveddevices;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.beariksonstudios.automatic.pushlocal.pushlocal.Prefs;
import com.beariksonstudios.automatic.pushlocal.pushlocal.R;
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.networkdiscovery.dialog.SyncDialog;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class SavedDevicesActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_devices);
        ListView list = (ListView) findViewById(R.id.saved_devices_list);

        final SharedPreferences prefs = getSharedPreferences(Prefs.SAVED_DEVICES_FILE, Activity.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(Prefs.HOSTNAME_SET, new HashSet<String>());
        final ArrayList<String> hostNames = new ArrayList<>();
        for (String s : set) {
            Log.d("PushLocal", "Retrieved host name: " + s);
            hostNames.add(s);
        }
        list.setAdapter(new SavedDevicesAdapter(this, R.layout.item_main_list, hostNames));
        final SavedDevicesActivity _this = this;
        Log.d("PushLocal", "item listener adding");
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                final int position = i;
                Toast.makeText(_this.getApplicationContext(), "Clicked", Toast.LENGTH_LONG);
                Log.d("PushLocal", "Clicked");
                Set<String> set = prefs.getStringSet(Prefs.ADDRESS_SET, new HashSet<String>());

                AsyncTask<Set<String>, Void, Void> task = new AsyncTask<Set<String>, Void, Void>() {
                    public Pair<String, InetAddress> device;

                    @Override
                    protected Void doInBackground(Set<String>... sets) {
                        Set<String> set = sets[0];
                        try {
                            InetAddress address = InetAddress.getByName(set.toArray(new String[set.size()])[position]);
                            device = new Pair<>(hostNames.get(position), address);
                        } catch (UnknownHostException e) {
                            Log.e("PushLocal", e.getMessage());
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        if (device != null) {
                            SyncDialog dialog = new SyncDialog(_this, device);
                            dialog.show();
                        }
                    }
                };
                task.execute(set);
            }
        });
        Log.d("PushLocal", "item listener added");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_saved_devices, menu);
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
