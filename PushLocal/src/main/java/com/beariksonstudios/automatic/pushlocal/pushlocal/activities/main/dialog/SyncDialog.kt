package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.ListView
import com.beariksonstudios.automatic.pushlocal.pushlocal.R
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.MainActivity
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Device

/**
 * Created by BrianErikson on 8/18/2015.
 */
class SyncDialog(val con: Context, val selectedDevice: Device) : Dialog(con) {

    override fun show() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_main_sync, null)

        setCanceledOnTouchOutside(true)
        setCancelable(true)

        super.setContentView(view)

        val list = view.findViewById(R.id.listView_network_dialog) as ListView
        list.adapter = SyncListAdapter(context, R.id.listView_network_dialog, selectedDevice)

        val button = view.findViewById(R.id.button_network_dialog) as Button

        val _this = this
        if (selectedDevice.connected) {
            button.text = "Disconnect"
            button.setOnClickListener {
                val intent = Intent()
                intent.putExtra(DISCONNECT_ACTION_IP_ADDRESS, selectedDevice.ipAddress)
                intent.action = DISCONNECT_ACTION
                _this.context.sendBroadcast(intent)
                _this.dismiss()
            }
        } else {
            button.text = "Sync"
            button.setOnClickListener {
                val intent = Intent()
                intent.putExtra(CONNECT_ACTION_IP_ADDRESS, selectedDevice.ipAddress)
                Log.d("Pushlocal", "Stored IP Address: " + selectedDevice.ipAddress)
                intent.action = CONNECT_ACTION
                _this.context.sendBroadcast(intent)
                _this.dismiss()
            }
        }


        super.show()
    }

    companion object {
        val CONNECT_ACTION = MainActivity.BROADCAST_PREFIX + "Connect"
        val CONNECT_ACTION_IP_ADDRESS = "IpAddress"
        val DISCONNECT_ACTION = MainActivity.BROADCAST_PREFIX + "Disconnect"
        val DISCONNECT_ACTION_IP_ADDRESS = "Disconnect IpAddress"
    }
}
