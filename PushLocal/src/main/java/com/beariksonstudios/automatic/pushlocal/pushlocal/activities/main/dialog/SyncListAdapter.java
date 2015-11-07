package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.dialog;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.beariksonstudios.automatic.pushlocal.pushlocal.PLDatabase;
import com.beariksonstudios.automatic.pushlocal.pushlocal.R;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Device;

/**
 * Created by BrianErikson on 8/18/2015.
 */
public class SyncListAdapter extends ArrayAdapter<String> {
    public static String[] choices = {
            "Notifications",
            "Text Messages",
            "Save this Device"
    };
    private final Device selectedDevice;
    private Context context;

    public SyncListAdapter(Context context, int resource, Device selectedDevice) {
        super(context, resource, choices);
        this.selectedDevice = selectedDevice;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {View view;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.item_dialog_sync_list, parent, false);
        }
        else
            view = convertView;

        TextView textView = (TextView) view.findViewById(R.id.textView_item_dialog_list);
        textView.setText(choices[position]);

        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox_item_dialog_list);
        // reset checkbox to prevent recyclable-view glitches
        checkBox.setChecked(false);
        checkBox.setOnClickListener(null);

        if(choices[position].equals(choices[2])){
            Log.d("Pushlocal", position + "   " + choices[position] + "   " + choices[2]);
            if(selectedDevice.isSaved) {
                checkBox.setChecked(true);
            }

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PLDatabase db = new PLDatabase(context);
                    if(checkBox.isChecked()) {
                        boolean success = db.insertDevice(selectedDevice);
                        if (success) {
                            selectedDevice.isSaved = true;
                            Toast.makeText(context, selectedDevice.hostName + " is now saved.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, selectedDevice.hostName + " could not be saved!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        boolean successfullyRemoved = db.removeDevice(selectedDevice.hostName);
                        if(successfullyRemoved){
                            selectedDevice.isSaved = false;
                            Toast.makeText(context, selectedDevice.hostName + " is now deleted.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, selectedDevice.hostName + " could not be deleted!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        return view;
    }
}
