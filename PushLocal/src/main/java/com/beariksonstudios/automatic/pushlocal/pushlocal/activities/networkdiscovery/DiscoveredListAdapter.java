package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.networkdiscovery;

import android.app.Activity;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.beariksonstudios.automatic.pushlocal.pushlocal.R;

import java.net.InetAddress;
import java.util.List;

/**
 * Created by BrianErikson on 8/17/2015.
 */
public class DiscoveredListAdapter extends ArrayAdapter<Pair<String, InetAddress>> {
    private Context context;
    private List<Pair<String, InetAddress>> devices;

    public DiscoveredListAdapter(Context context, int resource, List<Pair<String, InetAddress>> devices) {
        super(context, resource, devices);
        this.context = context;
        this.devices = devices;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.main_list_item, parent, false);
        TextView textView = (TextView) view.findViewById(R.id.list_item_text);
        textView.setText(devices.get(position).first);

        return view;
    }
}
