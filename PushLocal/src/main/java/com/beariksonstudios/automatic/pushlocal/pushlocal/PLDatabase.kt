package com.beariksonstudios.automatic.pushlocal.pushlocal

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Device

import java.util.ArrayList

/**
 * Created by BrianErikson on 9/5/15.
 */
class PLDatabase(context: Context) : SQLiteOpenHelper(context, PLDatabase.DB_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS $DEVICE_TABLE_NAME ($DEVICE_COLUMN_ID INTEGER PRIMARY KEY, $DEVICE_COLUMN_HOSTNAME TEXT, $DEVICE_COLUMN_IPADDRESS TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, i: Int, i1: Int) {
        // TODO: Upgrade more gracefully?
        db.execSQL("DROP TABLE IF EXISTS " + DEVICE_TABLE_NAME)
        onCreate(db)
    }

    fun insertDevice(device: Device): Boolean {
        if (deviceExists(device.hostName)) {
            return false // hostName already exists. Update instead?
        }
        val db = writableDatabase

        val values = ContentValues()
        values.put(DEVICE_COLUMN_HOSTNAME, device.hostName)
        values.put(DEVICE_COLUMN_IPADDRESS, device.ipAddress)
        val result = db.insert(DEVICE_TABLE_NAME, null, values)
        return result >= 0
    }

    fun updateDevice(device: Device): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put(DEVICE_COLUMN_HOSTNAME, device.hostName)
        values.put(DEVICE_COLUMN_IPADDRESS, device.ipAddress)
        return 0 < db.update(DEVICE_TABLE_NAME, values, DEVICE_COLUMN_HOSTNAME + "=?", arrayOf(device.hostName))
    }

    fun removeDevice(hostName: String): Boolean {
        val db = writableDatabase
        return 0 < db.delete(DEVICE_TABLE_NAME, DEVICE_COLUMN_HOSTNAME + "=?", arrayOf(hostName))
    }

    fun deviceExists(hostName: String): Boolean {
        val db = writableDatabase
        val cursor = db.query(DEVICE_TABLE_NAME, arrayOf(DEVICE_COLUMN_HOSTNAME), DEVICE_COLUMN_HOSTNAME + "=?",
                arrayOf(hostName), null, null, null)
        if (cursor.count > 0) {
            cursor.close()
            return true
        }
        cursor.close()
        return false
    }

    val savedDevices: ArrayList<Device>
        get() {
            val db = writableDatabase
            val cursor = db.query(DEVICE_TABLE_NAME, arrayOf(DEVICE_COLUMN_HOSTNAME, DEVICE_COLUMN_IPADDRESS),
                    null, null, null, null, DEVICE_COLUMN_HOSTNAME)

            val devices = ArrayList<Device>()
            val hostNameCI = cursor.getColumnIndex(DEVICE_COLUMN_HOSTNAME)
            val ipAddressCI = cursor.getColumnIndex(DEVICE_COLUMN_IPADDRESS)
            cursor.moveToFirst()
            for (i in 0..cursor.count - 1) {
                if (cursor.isAfterLast)
                    break
                devices.add(Device(cursor.getString(hostNameCI), cursor.getString(ipAddressCI), true, false, false))
                cursor.moveToNext()
            }

            return devices
        }

    companion object {
        private val DB_NAME = "PushLocal.DB"
        private val DEVICE_TABLE_NAME = "devices"
        private val DEVICE_COLUMN_ID = "id"
        private val DEVICE_COLUMN_IPADDRESS = "ipAddress"
        private val DEVICE_COLUMN_HOSTNAME = "hostName"
    }
}
