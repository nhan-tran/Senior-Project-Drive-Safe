package com.example.nhan.myapplication;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Location;

import com.example.nhan.myapplication.AppPrefs.AppPrefs;
import com.example.nhan.myapplication.SQLite.DriveSafeProvider;
import com.example.nhan.myapplication.SQLite.DrivingDataContract;
import com.google.android.gms.location.LocationClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class handles location updates that were initially requested by the LocationRequestor class.
 *
 */
public class LocationUpdatesIntentService extends IntentService {

    public LocationUpdatesIntentService(){
        super("LocationUpdatesIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // http://www.intelligrape.com/blog/background-location-updates-on-android/
        // OMG thank god... finally
        Location location = (Location) intent.getExtras().get(LocationClient.KEY_LOCATION_CHANGED);

        // write the location to db
        if (location != null)
        {
            DriveSafeProvider db = new DriveSafeProvider(this);
            ContentValues values = new ContentValues();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_CREATED_DATE, dateFormat.format(date));
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_SPEED, location.getSpeed());
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_LONGITUDE, location.getLongitude());
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_LATITUDE, location.getLatitude());
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_LOCATION_TIME, location.getTime());
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_BEARING, location.getBearing());
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_USER_ID, AppPrefs.GetUserMemberId());
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_ACCURACY, location.getAccuracy());

            db.InsertRecord(DrivingDataContract.LOCATION_LOG.TABLE_NAME, values);
        }
    }
}
