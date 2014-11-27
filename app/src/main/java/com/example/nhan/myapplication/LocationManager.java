package com.example.nhan.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.example.nhan.myapplication.SQLite.DAL;
import com.example.nhan.myapplication.SQLite.DrivingDataContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class LocationManager implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    Context mContext;
    //LocationClient mLocationClient;
    GoogleApiClient mClient;
    public Boolean mConnected;

    public LocationManager(Context context) {
        mContext = context;
        //mConnected = false;
        //mLocationClient = new LocationClient(mContext, this, this);
        // Connect the client.
        //mLocationClient.connect();

        mClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        ///mClient.connect();
    }

    public Location getLocation(){
        Location currentLocation = null;
        //FusedLocationProviderApi fusedProvider = LocationServices.FusedLocationApi;
        ConnectionResult result =  mClient.blockingConnect();
        if (result.isSuccess()) {
            mConnected = true;
            if (mClient.isConnected()){
                currentLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
                mClient.disconnect();
            }
        }
        else {
            mConnected = false;
        }


        return currentLocation;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mConnected = true;
        DAL dal = new DAL(this);
        int confidence = activity.getConfidence();
        int activityType = activity.getType();
        String activityName = getNameFromType(activityType);

        Location currentLocation = mLocationManager.getLocation();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        // Create a new map of values, where column names are the keys

        ContentValues values = new ContentValues();
        //values.put(DrivingDataContract.DrivingEntry.COLUMN_NAME_ENTRY_ID, "testId");
        values.put(DrivingDataContract.DrivingEntry.COLUMN_NAME_ACTIVITY_STATUS, activityName);
        values.put(DrivingDataContract.DrivingEntry.COLUMN_NAME_CREATED_DATE, dateFormat.format(date));
        values.put(DrivingDataContract.DrivingEntry.COLUMN_NAME_SPEED, currentLocation.getSpeed());
        values.put(DrivingDataContract.DrivingEntry.COLUMN_NAME_LONGITUDE, currentLocation.getLongitude());
        values.put(DrivingDataContract.DrivingEntry.COLUMN_NAME_LATITUDE, currentLocation.getLatitude());
        values.put(DrivingDataContract.DrivingEntry.COLUMN_NAME_LOCATION_TIME, currentLocation.getTime());
        values.put(DrivingDataContract.DrivingEntry.COLUMN_NAME_BEARING, currentLocation.getBearing());
        values.put(DrivingDataContract.DrivingEntry.COLUMN_NAME_USER_ID, "testUserId");
        values.put(DrivingDataContract.DrivingEntry.COLUMN_NAME_SYNCED, 0);
        values.put(DrivingDataContract.DrivingEntry.COLUMN_NAME_HAS_LOCATION, (currentLocation != null));
        values.put(DrivingDataContract.DrivingEntry.COLUMN_NAME_CONFIDENCE, confidence);
        values.put(DrivingDataContract.DrivingEntry.COLUMN_NAME_ACCURACY, currentLocation.getAccuracy());

        dal.WriteLog(values);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mConnected = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("LocationManager", "onConnectionFailed()");
    }
}
