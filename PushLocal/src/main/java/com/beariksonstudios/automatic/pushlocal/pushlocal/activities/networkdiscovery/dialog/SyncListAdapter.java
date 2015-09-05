package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.networkdiscovery.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.beariksonstudios.automatic.pushlocal.pushlocal.R;

/**
 * Created by BrianErikson on 8/18/2015.
 */
public class SyncListAdapter extends ArrayAdapter<String> {
    public static String[] choices = {
            "Notifications",
            "Text Messages"
    };
    private Context context;

    public SyncListAdapter(Context context, int resource) {
        super(context, resource, choices);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.item_network_dialog_list, parent, false);
        } else
            view = convertView;

        TextView textView = (TextView) view.findViewById(R.id.textView_item_network_dialog_list);
        textView.setText(choices[position]);

        return view;
    }


}
