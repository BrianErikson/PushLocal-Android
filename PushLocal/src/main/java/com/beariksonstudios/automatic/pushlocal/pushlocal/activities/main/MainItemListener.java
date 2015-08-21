package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import com.beariksonstudios.automatic.pushlocal.pushlocal.R;
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.networkdiscovery.NetworkDisoveryActivity;

/**
 * Created by nphel on 8/15/2015.
 */
public class MainItemListener implements OnItemClickListener {


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView textView = (TextView) view.findViewById(R.id.list_item_text);

        String stringText = textView.getText().toString();
        if(stringText.contains("Network Discovery")){
            Intent intent = new Intent(view.getContext(), NetworkDisoveryActivity.class);
            view.getContext().startActivity(intent);
        }
        else if (stringText.contains("Test Notification")) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(view.getContext())
                    .setSmallIcon(android.R.drawable.arrow_up_float)
                    .setContentText("This is a test notification")
                    .setContentTitle("TEST NOTIFICATION")
                    .setSubText("subText of Test");
            NotificationManager notificationManager = (NotificationManager) view.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1234, builder.build());
        }
    }
}
