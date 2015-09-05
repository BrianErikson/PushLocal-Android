package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.saveddevices;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.beariksonstudios.automatic.pushlocal.pushlocal.R;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Device;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nphel on 8/28/2015.
 */
public class SavedDevicesAdapter extends ArrayAdapter<Device> {
    private Context context;
    private List<Device> devices;

    public SavedDevicesAdapter(Context context, int resource, ArrayList<Device> devices) {
        super(context, resource, devices);
        this.context = context;
        this.devices = devices;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.item_main_list, parent, false);
        } else
            view = convertView;

        TextView textView = (TextView) view.findViewById(R.id.list_item_text);
        Log.d("PushLocal", "adding to list: " + devices.get(position));
        textView.setText(devices.get(position).hostName);

        return view;
    }
}
