package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main.dialog

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.beariksonstudios.automatic.pushlocal.pushlocal.PLDatabase
import com.beariksonstudios.automatic.pushlocal.pushlocal.R
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Device

/**
 * Created by BrianErikson on 8/18/2015.
 */
class SyncListAdapter(private val con: Context, resource: Int, private val selectedDevice: Device)
: ArrayAdapter<String>(con, resource, SyncListAdapter.choices) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.item_dialog_sync_list, parent, false)
        } else
            view = convertView

        val textView = view.findViewById(R.id.textView_item_dialog_list) as TextView
        textView.text = choices[position]

        val checkBox = view.findViewById(R.id.checkbox_item_dialog_list) as CheckBox
        // reset checkbox to prevent recyclable-view glitches
        checkBox.isChecked = false
        checkBox.setOnClickListener(null)

        if (choices[position] == choices[2]) {
            Log.d("Pushlocal", "$position  ${choices[position]} ${choices[2]}")
            if (selectedDevice.isSaved) {
                checkBox.isChecked = true
            }

            checkBox.setOnClickListener {
                val db = PLDatabase(context)
                if (checkBox.isChecked) {
                    val success = db.insertDevice(selectedDevice)
                    if (success) {
                        selectedDevice.isSaved = true
                        Toast.makeText(context, selectedDevice.hostName + " is now saved.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, selectedDevice.hostName + " could not be saved!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val successfullyRemoved = db.removeDevice(selectedDevice.hostName)
                    if (successfullyRemoved) {
                        selectedDevice.isSaved = false
                        Toast.makeText(context, selectedDevice.hostName + " is now deleted.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, selectedDevice.hostName + " could not be deleted!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return view
    }

    companion object {
        val choices = arrayOf("Notifications", "Text Messages", "Save this Device")
    }
}
