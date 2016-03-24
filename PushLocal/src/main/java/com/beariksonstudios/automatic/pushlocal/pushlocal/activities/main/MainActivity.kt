package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main

import android.app.AlertDialog
import android.content.*
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.ActionBarActivity
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.beariksonstudios.automatic.pushlocal.pushlocal.PLDatabase
import com.beariksonstudios.automatic.pushlocal.pushlocal.R
import com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.dialog.SyncDialog
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Device
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Server

import java.util.ArrayList
import java.util.Timer


class MainActivity : ActionBarActivity() {
    var connectedToWifi: Boolean = false
    private set(value) {
        if (value != field && value) {
            Log.d("PushLocal", "Connected to WiFi")
            autoConnect()
        }
        else {
            Log.d("PushLocal", "Disconnected from WiFi")
        }
        field = value
    }

    var devices: ArrayList<Device> = ArrayList()
    private val savedDevices: ArrayList<Device> = ArrayList()
    get() {
        if (field.size < 1) {
            val db: PLDatabase = PLDatabase(this)
            field.addAll(db.savedDevices)
        }
        return field
    }
    private var broadcastReceiver: BroadcastReceiver? = null
    private var listAdapter: MainListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val _this = this
        context = applicationContext
        setContentView(R.layout.activity_main)

        val list = findViewById(R.id.mainmenu_list) as ListView

        devices.addAll(savedDevices)
        listAdapter = MainListAdapter(this, R.layout.item_main_list, devices)
        list.adapter = listAdapter
        list.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val syncDialog = SyncDialog(_this, devices[position])
            syncDialog.setOnDismissListener { listAdapter!!.notifyDataSetChanged() }
            syncDialog.show()
        }

        val discover = findViewById(R.id.mainmenu_discoverbuttton) as Button
        val timer = Timer()
        discover.setOnClickListener {
            timer.schedule(DiscoveryTimer(devices, _this), 5000)
            sendBroadcast(Intent().setAction(BROADCAST_ACTION))
            Toast.makeText(_this, "Searching for Devices... :)", Toast.LENGTH_SHORT).show()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val notificationIntent = Intent(this, NotificationListener::class.java)
            startService(notificationIntent)
        } else {
            val accessibilityIntent = Intent(this, AccessibilityService::class.java)
            startService(accessibilityIntent)
        }
        listAdapter!!.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        broadcastReceiver = BroadcastReceiver()
        val filter = IntentFilter()
        filter.addAction(Server.NEW_DEVICE_ACTION)
        filter.addAction(Server.CONNECTED_DEVICE_ACTION)
        filter.addAction(Server.CONFIRMED_DISCONNECT_ACTION)

        registerReceiver(broadcastReceiver, filter)
        registerReceiver(WifiReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        val intent = Intent()
        intent.action = REQUEST_DEVICES_ACTION
        sendBroadcast(intent)
        val serviceName = startService(Intent(this, Server::class.java))
        if (serviceName != null)
            Log.d("PushLocal", "Started or confirmed that Server service is running")
        else
            Toast.makeText(this, "ERROR: Could not start server", Toast.LENGTH_LONG).show()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!isServiceEnabled)
                showEnableDialog()
        } else {
            if (!isAccessibilitySettingsOn(this)) {
                showAccessibilityEnabledDialog()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun autoConnect() {
        for (device in savedDevices) {
            SyncDialog.syncToDevice(this, device)
        }
    }

    private fun showEnableDialog() {
        AlertDialog.Builder(this).setMessage("Please enable NotificationMonitor access").setTitle("Notification Access").setIconAttribute(android.R.attr.alertDialogIcon).setCancelable(true).setPositiveButton(android.R.string.ok
        ) { dialog, id -> startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")) }.setNegativeButton(android.R.string.cancel
        ) { dialog, id -> }.create().show()
    }

    private fun showAccessibilityEnabledDialog() {
        AlertDialog.Builder(this).setMessage("Please enable accessibility service access").setTitle("Accessibility Access").setIconAttribute(android.R.attr.alertDialogIcon).setCancelable(true).setPositiveButton(android.R.string.ok
        ) { dialog, id -> startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }.setNegativeButton(android.R.string.cancel
        ) { dialog, id -> }.create().show()
    }

    private val isServiceEnabled: Boolean
    get() {
        val pkgName = packageName
        val flat = Settings.Secure.getString(contentResolver,
                "enabled_notification_listeners")
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in names.indices) {
                val cn = ComponentName.unflattenFromString(names[i])
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private inner class BroadcastReceiver : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Server.NEW_DEVICE_ACTION) {
                val device = Device(intent.getStringExtra(Server.NEW_DEVICE_ACTION_HOSTNAME),
                        intent.getStringExtra(Server.NEW_DEVICE_ACTION_IP_ADDRESS), false, intent.getBooleanExtra(Server.NEW_DEVICE_ACTION_STATE, false), true)
                var exists = false
                for (d in devices) {
                    if (d.hostName == device.hostName) {
                        exists = true
                        d.isDiscovered = true
                        if (d.ipAddress != device.ipAddress) {
                            d.ipAddress = device.ipAddress
                            if (d.isSaved) {
                                val pd = PLDatabase(context)
                                pd.updateDevice(device)
                            }
                        }
                    }
                }
                if (!exists)
                    devices.add(device)

                listAdapter!!.notifyDataSetChanged()
            } else if (intent.action == Server.CONNECTED_DEVICE_ACTION) {
                val ipAddress = intent.getStringExtra(Server.CONNECTED_DEVICE_ACTION_IPADDRESS)
                Log.d("PushLocal", "CONNECTED_DEVICE_ACTION for ip $ipAddress. Now iterating through devices")
                for (d in devices) {
                    if (d.ipAddress == ipAddress) {
                        d.connected = true
                        d.isDiscovered = true
                        listAdapter!!.notifyDataSetChanged()
                        Toast.makeText(context, "${d.hostName} is now connected", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (intent.action == Server.CONFIRMED_DISCONNECT_ACTION) {
                val ipAddress = intent.getStringExtra(Server.CONFIRMED_DISCONNECT_ACTION_IPADDRESS)
                for (d in devices) {
                    if (d.ipAddress == ipAddress) {
                        d.connected = false
                        listAdapter!!.notifyDataSetChanged()
                        Toast.makeText(context, "${d.hostName} is now disconnected", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private inner class WifiReceiver : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val conMan : ConnectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager? ?: throw RuntimeException("Could not access Connectivity Manager")

            val netInfo : NetworkInfo? = conMan.activeNetworkInfo
            if (netInfo?.type == ConnectivityManager.TYPE_WIFI) {
                connectedToWifi = true
            }
            else {
                connectedToWifi = false
            }
        }
    }

    companion object {
        val BROADCAST_ACTION = MainActivity.BROADCAST_PREFIX + "Broadcast"
        val REQUEST_DEVICES_ACTION = MainActivity.BROADCAST_PREFIX + "Request Devices"
        val BROADCAST_PREFIX = "PushLocal."
        var context: Context? = null
            private set

        fun isAccessibilitySettingsOn(mContext: Context): Boolean {
            var accessibilityEnabled = 0
            val service = "com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.AccessibilityService"
            try {
                accessibilityEnabled = Settings.Secure.getInt(
                        mContext.applicationContext.contentResolver,
                        android.provider.Settings.Secure.ACCESSIBILITY_ENABLED)

            } catch (e: Settings.SettingNotFoundException) {

            }

            val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')

            if (accessibilityEnabled == 1) {
                val settingValue = Settings.Secure.getString(
                        mContext.applicationContext.contentResolver,
                        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
                if (settingValue != null) {
                    mStringColonSplitter.setString(settingValue)
                    while (mStringColonSplitter.hasNext()) {
                        val accessabilityService = mStringColonSplitter.next()
                        if (accessabilityService.equals(service, ignoreCase = true)) {
                            return true
                        }
                    }
                }
            }
            return false
        }
    }
}