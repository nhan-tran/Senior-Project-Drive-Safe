package com.example.nhan.myapplication;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.nhan.myapplication.Enums.RequestType;
import com.example.nhan.myapplication.SQLite.DriveSafeProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Nhan on 12/11/2014.
 */
public class LocationRequestor implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener
{
    Context mContext;
    GoogleApiClient mClient;
    Boolean mConnected;
    PendingIntent mLocationUpdatesPendingIntent;
    RequestType mRequestType;

    static final int UPDATE_INTERVAL = 1000 * 5;   // ms * number of seconds

    public LocationRequestor(Context context, RequestType requestType) {
        mContext = context;
        mRequestType = requestType;

        mClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mConnected = true;
        DriveSafeProvider driveSafeProvider = new DriveSafeProvider(mContext);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);

        Intent intent = new Intent(mContext, LocationUpdatesIntentService.class);

        mLocationUpdatesPendingIntent =
                PendingIntent.getService(mContext, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        switch (mRequestType) {
            case START:
                LocationServices.FusedLocationApi.requestLocationUpdates(mClient, locationRequest, mLocationUpdatesPendingIntent);
                break;
            case STOP:
                LocationServices.FusedLocationApi.removeLocationUpdates(mClient, mLocationUpdatesPendingIntent);
                break;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mConnected = false;
        Log.d("LocationRequestor", "LocationRequestor onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("LocationRequestor", "LocationRequestor onConnectionFailed()");
    }

}