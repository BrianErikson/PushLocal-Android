package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.saveddevices;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.beariksonstudios.automatic.pushlocal.pushlocal.R;
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.MainActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nphel on 8/28/2015.
 */
public class SavedDevicesAdapter extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> hostNames = new ArrayList<>();

    public SavedDevicesAdapter(Context context, int resource){
        super(context, resource);
        this.context = context;
        SharedPreferences prefs = context.getSharedPreferences(MainActivity.SAVED_DEVICES_FILE, Activity.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet("hostNames", new HashSet<String>());
        for (String s : set) {
            hostNames.add(s);
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) convertView.findViewById(R.id.list_item_text);
        textView.setText(hostNames.get(position));

        return convertView;
    }
}
