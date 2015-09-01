package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.saveddevices;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.beariksonstudios.automatic.pushlocal.pushlocal.Prefs;
import com.beariksonstudios.automatic.pushlocal.pushlocal.R;
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.MainActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nphel on 8/28/2015.
 */
public class SavedDevicesAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> hostNames;

    public SavedDevicesAdapter(Context context, int resource, List<String> hostNames){
        super(context, resource, hostNames);
        this.context = context;
        this.hostNames = hostNames;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.item_main_list, parent, false);
        }
        else
            view = convertView;

        TextView textView = (TextView) view.findViewById(R.id.list_item_text);
        Log.d("PushLocal", "adding to list: " + hostNames.get(position));
        textView.setText(hostNames.get(position));

        return view;
    }
}
