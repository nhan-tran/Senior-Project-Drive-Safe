package com.example.nhan.myapplication;

/**
 * Created by Nhan on 11/19/2014.
 */

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.example.nhan.myapplication.Enums.RequestType;
import com.example.nhan.myapplication.SQLite.DAL;
import com.example.nhan.myapplication.SQLite.DrivingDataContract;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Service that receives ActivityRecognition updates. It receives
 * updates in the background, even if the main Activity is not visible.
 */
public class ActivityRecognitionIntentService extends IntentService {

    LocationManager mLocationManager;
    DAL db;
    static final int MIN_CONFIDENCE_LVL = 50;

    public ActivityRecognitionIntentService()
    {
        super("ActivityRecognitionIntentService");
        db = new DAL(this);
        Boolean x = true;
    }
    /**
     * Called when a new activity detection update is available.
     */
    @Override
    protected void onHandleIntent(Intent intent)
    {
        // If the incoming intent contains an update
        if (ActivityRecognitionResult.hasResult(intent))
        {
            // Get the update
            ActivityRecognitionResult result =
                    ActivityRecognitionResult.extractResult(intent);

            ActivityDeterminator(result);
        }
        else
        {
            /*
             * Do nothing, there was not an activity update.
             * Might want to error handle this later on in case activity updates somehow stopped coming but we're still getting intents
             */
        }
    }


    public void ActivityDeterminator(ActivityRecognitionResult result) {
        LocationRequestor requestor;
        int previousActivityType = DetectedActivity.UNKNOWN;    // set default last activity to UNKNOWN

        // Restore preferences
        Context ctx = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        Boolean isDrivingForSure = prefs.getBoolean("isDrivingForSure", false);

        // Get the most probable activity
        DetectedActivity newestActivity = result.getMostProbableActivity();

        // check for confidence level
        if (newestActivity.getConfidence() < MIN_CONFIDENCE_LVL) {
            // if the confidence is less than 50 we will not accept it
            newestActivity = new DetectedActivity(DetectedActivity.UNKNOWN, 100);
        }

        // testing in_vehicle
        // newestActivity = new DetectedActivity(DetectedActivity.IN_VEHICLE, 100);

        // get latest/last activity logged
        Cursor cursor = db.GetLatestSessionActivities();
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            previousActivityType = cursor.getInt(2);    // get the last activity type
        }
        cursor.close();

        if (newestActivity.getType() == DetectedActivity.IN_VEHICLE) {
            if (previousActivityType == DetectedActivity.IN_VEHICLE && !isDrivingForSure) {
                // the shift has happen! We went from something to now detected in_vehicle twice

                // intializing a LocationRequestor with RequestType.START will automatically make the request for location updates
                requestor = new LocationRequestor(this, RequestType.START);
                // TODO if this thread dies before LocationRequestor is able to connect?
                // TODO If so, spin up a new async thread to make the request

                isDrivingForSure = true;
                SharedPreferences.Editor prefsEdit = prefs.edit();
                prefsEdit.putBoolean("isDrivingForSure", isDrivingForSure);
                prefsEdit.commit(); // commit the edit!

                //Boolean confirm_isDrivingForSure = prefs.getBoolean("isDrivingForSure", false);

            } else {
                // either the previous activity was not in_vehicle or we are already isDrivingForSure (so we're already logging)
            }
        } else if (isDrivingForSure)  // isDrivingForSure is true so we're logging but this newestActivity is not in_vehicle
        {
            if (previousActivityType != DetectedActivity.IN_VEHICLE) {
                // if the previousActivity was also not in_vehicle then we have detect two activities where it's not in_vehicle so turn off location logging
                requestor = new LocationRequestor(this, RequestType.STOP);
                isDrivingForSure = false;
                SharedPreferences.Editor prefsEdit = prefs.edit();
                prefsEdit.putBoolean("isDrivingForSure", isDrivingForSure);
                prefsEdit.commit(); // commit the edit!
            }
        }
        else {
            // don't really need this else since all it needs to do is log the newestActivity which we'll do anyways at the bottom
        }

        // Log the newest activity
        ContentValues values = new ContentValues();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        values.put(DrivingDataContract.SESSION_ACTIVITIES.COLUMN_NAME_ACTIVITY_STATUS, getNameFromType(newestActivity.getType()));
        values.put(DrivingDataContract.SESSION_ACTIVITIES.COLUMN_NAME_ACTIVITY_TYPE, newestActivity.getType());
        values.put(DrivingDataContract.SESSION_ACTIVITIES.COLUMN_NAME_CREATED_DATE, dateFormat.format(date));
        values.put(DrivingDataContract.SESSION_ACTIVITIES.COLUMN_NAME_CONFIDENCE, newestActivity.getConfidence());
        values.put(DrivingDataContract.SESSION_ACTIVITIES.COLUMN_NAME_IS_DRIVING, isDrivingForSure);

        db.InsertRecord(DrivingDataContract.SESSION_ACTIVITIES.TABLE_NAME, values);
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