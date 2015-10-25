package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.AvoidXfermode;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
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
        ImageView statusIndicator = (ImageView) view.findViewById(R.id.mainmenu_list_statusIndicator);
        if(device.connected){
            statusIndicator.setBackgroundResource(R.drawable.online_indicator);
        }
        else{
            statusIndicator.setBackgroundResource(R.drawable.offline_indicator);
        }

        TextView textView = (TextView) view.findViewById(R.id.mainmenu_list_devicename);
        if(!device.isDiscovered){
            textView.setTextColor(Color.GRAY);
        }else{
           textView.setTextColor(Color.BLACK);
        }
        textView.setText(device.hostName);

        ImageView imageView = (ImageView) view.findViewById(R.id.mainmenu_list_devicestateindicator);
        if(!device.isSaved){
            imageView.setImageDrawable(null);
        }

        return view;
    }


}
