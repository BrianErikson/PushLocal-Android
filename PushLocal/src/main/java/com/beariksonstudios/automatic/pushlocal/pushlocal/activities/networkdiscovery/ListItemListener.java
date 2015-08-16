package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.networkdiscovery;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import com.beariksonstudios.automatic.pushlocal.pushlocal.R;

/**
 * Created by nphel on 8/16/2015.
 */
public class ListItemListener implements AdapterView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView textView = (TextView) view.findViewById(R.id.list_item_text);

        String stringText = textView.getText().toString();
        Toast.makeText(view.getContext(), stringText, Toast.LENGTH_LONG);
        if(stringText.contains("")){
            Intent intent = new Intent(view.getContext(), NetworkDisoveryActivity.class);
            view.getContext().startActivity(intent);
        }
    }
}

