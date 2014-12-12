package com.example.nhan.myapplication;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationClient;

/**
 * Created by Nhan on 12/11/2014.
 */
public class LocationUpdatesIntentService extends IntentService {

    public LocationUpdatesIntentService(){
        super("LocationUpdatesIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // http://www.intelligrape.com/blog/background-location-updates-on-android/
        // OMG thank god... finally
        
        // not sure how to get location data out of this intent yet.
        Boolean x = true; // let's pause and see...
        Location location = (Location) intent.getExtras().get(LocationClient.KEY_LOCATION_CHANGED);
    }
}
