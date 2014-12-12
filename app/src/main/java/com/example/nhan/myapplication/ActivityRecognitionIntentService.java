package com.example.nhan.myapplication;

/**
 * Created by Nhan on 11/19/2014.
 */

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;

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
    }
    /**
     * Called when a new activity detection update is available.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        //Intent sendIntent = new Intent(this, MainActivity.class);


        // If the incoming intent contains an update
        if (ActivityRecognitionResult.hasResult(intent)) {
            // Get the update
            ActivityRecognitionResult result =
                    ActivityRecognitionResult.extractResult(intent);

            ActivityDeterminator(result);

        } else {
            /*
             * Do nothing, there was not an activity update.
             * Might want to error handle this later on in case activity updates somehow stopped coming but we're still getting intents
             */
        }
    }


    public void ActivityDeterminator(ActivityRecognitionResult result) {
        int previousActivityType = DetectedActivity.UNKNOWN;    // set default last activity to UNKNOWN
        Boolean isDrivingForSure = true;   // refactor this to Shared Preferences later

        // Get the most probable activity
        DetectedActivity newestActivity = result.getMostProbableActivity();

        // check for confidence level
        if (newestActivity.getConfidence() < MIN_CONFIDENCE_LVL) {
            // if the confidence is less than 50 we will not accept it
            newestActivity = new DetectedActivity(DetectedActivity.UNKNOWN, 100);
        }

        // get latest/last activity logged
        Cursor cursor = db.LatestSessionActivities();
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            previousActivityType = cursor.getInt(2);    // get the last activity type
        }
        cursor.close();

        if (newestActivity.getType() == DetectedActivity.IN_VEHICLE) {
            if (previousActivityType == DetectedActivity.IN_VEHICLE && !isDrivingForSure) {
                // the shift has happen! We went from something to now detected in_vehicle twice

            } else {
                // either the previous activity was not in_vehicle or we are already isDrivingForSure (so we're already logging)
            }
        } else if (isDrivingForSure)  // isDrivingForSure is true so we're logging but this newestActivity is not in_vehicle
        {
            if (previousActivityType != DetectedActivity.IN_VEHICLE) {
                // if the previousActivity was also not in_vehicle then we have detect two activities where it's not in_vehicle so turn off location logging

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

        db.InsertSessionActivity(values);
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