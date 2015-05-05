package com.example.nhan.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.example.nhan.myapplication.AppPrefs.AppPrefs;
import com.example.nhan.myapplication.SQLite.DriveSafeProvider;
import com.example.nhan.myapplication.SQLite.DrivingDataContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 3/18/15 This class is not used - The class used for getting location is LocationRequestor
 */
public class LocationManager implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    Context mContext;
    //LocationClient mLocationClient;
    GoogleApiClient mClient;
    DetectedActivity mMostProbableActivity;
    public Boolean mConnected;

    public LocationManager(Context context, DetectedActivity mostProbableActivity) {
        mContext = context;
        //mConnected = false;
        //mLocationClient = new LocationClient(mContext, this, this);
        // Connect the client.
        //mLocationClient.connect();
        mMostProbableActivity = mostProbableActivity;

        mClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mClient.connect();
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
        DriveSafeProvider driveSafeProvider = new DriveSafeProvider(mContext);
        int confidence = mMostProbableActivity.getConfidence();
        int activityType = mMostProbableActivity.getType();
        String activityName = getNameFromType(activityType);
        Location currentLocation = null;

        //Location currentLocation = mLocationManager.getLocation();
        if (mClient.isConnected()){
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
        }

        // Create a new map of values, where column names are the keys and write to DB if currentLocation is available
        if (currentLocation != null){
            ContentValues values = new ContentValues();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_CREATED_DATE, dateFormat.format(date));
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_SPEED, currentLocation.getSpeed());
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_LONGITUDE, currentLocation.getLongitude());
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_LATITUDE, currentLocation.getLatitude());
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_LOCATION_TIME, currentLocation.getTime());
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_BEARING, currentLocation.getBearing());
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_ANDROID_ID, AppPrefs.GetAndroidId());
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_MEMBERSHIP_ID, AppPrefs.GetUserMemberId());
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_SYNCED, 0);
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_ACCURACY, currentLocation.getAccuracy());

            driveSafeProvider.WriteLog(values);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mConnected = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("LocationManager", "onConnectionFailed()");
    }

    /**
     * Map detected activity types to strings
     *@param activityType The detected activity type
     *@return A user-readable name for the type
     */
    private String getNameFromType(int activityType) {
        switch(activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.TILTING:
                return "tilting";
            case DetectedActivity.RUNNING:
                return "running";
            case DetectedActivity.WALKING:
                return "walking";
        }
        return "unknown";
    }
}
