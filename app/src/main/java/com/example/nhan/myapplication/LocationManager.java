package com.example.nhan.myapplication;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationServices;

/**
 *
 */
public class LocationManager implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    Context mContext;
    //LocationClient mLocationClient;
    GoogleApiClient mClient;
    Boolean mConnected;

    public LocationManager(Context context) {
        mContext = context;
        mConnected = false;
        //mLocationClient = new LocationClient(mContext, this, this);
        // Connect the client.
        //mLocationClient.connect();

        mClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mClient.connect();
    }

    public Location getLocation(){
        Location currentLocation = null;
        FusedLocationProviderApi fusedProvider = LocationServices.FusedLocationApi;
        if (mConnected){
            currentLocation = fusedProvider.getLastLocation(mClient);
        }

        return currentLocation;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mConnected = true;
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
