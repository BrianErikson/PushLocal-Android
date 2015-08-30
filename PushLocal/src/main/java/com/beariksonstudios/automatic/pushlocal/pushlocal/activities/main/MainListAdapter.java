package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.beariksonstudios.automatic.pushlocal.pushlocal.R;

import java.util.List;

/**
 * Created by nphel on 8/15/2015.
 */
public class MainListAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> strings;

    public MainListAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.strings = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_main_list, parent, false);
        TextView textView = (TextView) view.findViewById(R.id.list_item_text);
        textView.setText(strings.get(position));

        return view;
    }


}
