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

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nphel on 8/28/2015.
 */
public class SavedDevicesAdapter extends ArrayAdapter<String> {
    private Context context;
    private String[] hostNames;
    public SavedDevicesAdapter(Context context, int resource){
        super(context, resource);
        this.context = context;
        SharedPreferences prefs = context.getSharedPreferences(MainActivity.SAVED_DEVICES_FILE, Activity.MODE_PRIVATE);
        hostNames = (String[]) prefs.getStringSet("hostNames", new HashSet<String>()).toArray();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_main_list, parent, false);
        TextView textView = (TextView) view.findViewById(R.id.list_item_text);
        textView.setText(hostNames[position]);

        return super.getView(position, convertView, parent);
    }
}
