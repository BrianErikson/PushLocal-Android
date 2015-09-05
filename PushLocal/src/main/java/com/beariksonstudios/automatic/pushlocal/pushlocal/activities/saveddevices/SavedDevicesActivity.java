package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.saveddevices;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.beariksonstudios.automatic.pushlocal.pushlocal.PLDatabase;
import com.beariksonstudios.automatic.pushlocal.pushlocal.R;
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.networkdiscovery.dialog.SyncDialog;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Device;

import java.util.ArrayList;


public class SavedDevicesActivity extends ActionBarActivity {
    private ArrayList<Device> devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_devices);
        ListView list = (ListView) findViewById(R.id.saved_devices_list);

        PLDatabase db = new PLDatabase(this);
        devices = db.getDevices();

        list.setAdapter(new SavedDevicesAdapter(this, R.layout.item_main_list, devices));
        final SavedDevicesActivity _this = this;
        Log.d("PushLocal", "item listener adding");
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                SyncDialog dialog = new SyncDialog(_this, devices.get(i));
                dialog.show();
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
