package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.AvoidXfermode;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import com.beariksonstudios.automatic.pushlocal.pushlocal.R;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Device;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nphel on 8/15/2015.
 */
public class MainListAdapter extends ArrayAdapter<Device> {
    private Context context;
    private ArrayList<Device> devices;

    public MainListAdapter(Context context, int resource, ArrayList<Device> devices) {
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

        Device device = devices.get(position);
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.mainmenu_list_radioButton);
        int color = R.color.radialButton_offline_color;
        if(device.connected){
            color = R.color.radialButton_online_color;
        }
        radioButton.setButtonTintList(ColorStateList.valueOf(color));

        TextView textView = (TextView) view.findViewById(R.id.mainmenu_list_devicename);
        textView.setText(device.hostName);

        ImageView imageView = (ImageView) view.findViewById(R.id.mainmenu_list_devicestateindicator);
        if(!device.isSaved){
            imageView.setImageDrawable(null);
        }

        return view;
    }


}
