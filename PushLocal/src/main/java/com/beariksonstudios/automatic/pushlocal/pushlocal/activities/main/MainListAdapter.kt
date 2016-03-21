package com.beariksonstudios.automatic.pushlocal.pushlocal.activities.main

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.AvoidXfermode
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.beariksonstudios.automatic.pushlocal.pushlocal.R
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Device

import java.util.ArrayList

/**
 * Created by nphel on 8/15/2015.
 */
class MainListAdapter(private val con: Context, resource: Int, private val devices: ArrayList<Device>)
: ArrayAdapter<Device>(con, resource, devices) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        if (convertView == null) {
            val inflater = (context as Activity).layoutInflater
            view = inflater.inflate(R.layout.item_main_list, parent, false)
        } else
            view = convertView

        val device = devices[position]

        // update connected icon
        val statusIndicator = view.findViewById(R.id.mainmenu_list_statusIndicator) as ImageView
        if (device.connected) {
            statusIndicator.setBackgroundResource(R.drawable.online_indicator)
        } else {
            statusIndicator.setBackgroundResource(R.drawable.offline_indicator)
        }

        // update online text color
        val textView = view.findViewById(R.id.mainmenu_list_devicename) as TextView
        if (!device.isDiscovered) {
            textView.setTextColor(Color.GRAY)
        } else {
            textView.setTextColor(Color.BLACK)
        }
        textView.text = device.hostName

        // update saved image
        val imageView = view.findViewById(R.id.mainmenu_list_devicestateindicator) as ImageView
        if (device.isSaved)
            imageView.setImageResource(R.drawable.abc_ic_menu_copy_mtrl_am_alpha)
        else
            imageView.setImageDrawable(null)

        return view
    }
}
