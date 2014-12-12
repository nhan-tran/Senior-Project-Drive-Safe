package com.example.nhan.myapplication;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.example.nhan.myapplication.SQLite.DAL;
import com.example.nhan.myapplication.SQLite.DrivingDataContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        static final int UPDATE_INTERVAL = 1000 * 5;   // ms * number of seconds

        public LocationRequestor(Context context) {
            mContext = context;
            mClient = new GoogleApiClient.Builder(mContext)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            mClient.connect();
        }

    public void requestUpdates()
    {
       // LocationServices.FusedLocationApi.
    }

    @Override
    public void onConnected(Bundle bundle) {
        mConnected = true;
        DAL dal = new DAL(mContext);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);

        Intent intent = new Intent(mContext, LocationUpdatesIntentService.class);

        mLocationUpdatesPendingIntent =
                PendingIntent.getService(mContext, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, locationRequest, mLocationUpdatesPendingIntent);
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