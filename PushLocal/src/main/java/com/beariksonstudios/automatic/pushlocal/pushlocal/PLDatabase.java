package com.beariksonstudios.automatic.pushlocal.pushlocal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.beariksonstudios.automatic.pushlocal.pushlocal.server.Device;

import java.util.ArrayList;

/**
 * Created by BrianErikson on 9/5/15.
 */
public class PLDatabase extends SQLiteOpenHelper {
    public static final String DB_NAME = "PushLocal.DB";
    public static final String DEVICE_TABLE_NAME = "devices";
    public static final String DEVICE_COLUMN_ID = "id";
    public static final String DEVICE_COLUMN_IPADDRESS = "ipAddress";
    public static final String DEVICE_COLUMN_HOSTNAME = "hostName";

    public PLDatabase(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DEVICE_TABLE_NAME +
                " (" + DEVICE_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                DEVICE_COLUMN_HOSTNAME + " TEXT, " +
                DEVICE_COLUMN_IPADDRESS + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // TODO: Upgrade more gracefully?
        db.execSQL("DROP TABLE IF EXISTS " + DEVICE_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertDevice(Device device) {
        if (deviceExists(device.hostName)) {
            return false; // hostName already exists. Update instead?
        }
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DEVICE_COLUMN_HOSTNAME, device.hostName);
        values.put(DEVICE_COLUMN_IPADDRESS, device.ipAddress);
        long result = db.insert(DEVICE_TABLE_NAME, null, values);
        return result >= 0;
    }

    public boolean updateDevice(Device device) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DEVICE_COLUMN_HOSTNAME, device.hostName);
        values.put(DEVICE_COLUMN_IPADDRESS, device.ipAddress);
        return 0 < db.update(DEVICE_TABLE_NAME, values, DEVICE_COLUMN_HOSTNAME + "=?", new String[]{device.hostName});
    }

    public boolean removeDevice(String hostName) {
        SQLiteDatabase db = getWritableDatabase();
        return 0 < db.delete(DEVICE_TABLE_NAME, DEVICE_COLUMN_HOSTNAME + "=?", new String[]{hostName});
    }

    public boolean deviceExists(String hostName) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(DEVICE_TABLE_NAME, new String[]{DEVICE_COLUMN_HOSTNAME}, DEVICE_COLUMN_HOSTNAME + "=?",
                new String[]{hostName}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public ArrayList<Device> getDevices() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(DEVICE_TABLE_NAME, new String[]{DEVICE_COLUMN_HOSTNAME, DEVICE_COLUMN_IPADDRESS},
                null, null, null, null, DEVICE_COLUMN_HOSTNAME);

        ArrayList<Device> devices = new ArrayList<>();
        int hostNameCI = cursor.getColumnIndex(DEVICE_COLUMN_HOSTNAME);
        int ipAddressCI = cursor.getColumnIndex(DEVICE_COLUMN_IPADDRESS);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            if (cursor.isAfterLast())
                break;
            devices.add(new Device(cursor.getString(hostNameCI), cursor.getString(ipAddressCI), true, false));
            cursor.moveToNext();
        }

        return devices;
    }
}
