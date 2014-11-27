package com.example.nhan.myapplication;

/**
 * Created by Nhan on 11/19/2014.
 */

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
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

    public ActivityRecognitionIntentService()
    {
        super("ActivityRecognitionIntentService");
    }
    /**
     * Called when a new activity detection update is available.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        //Intent sendIntent = new Intent(this, MainActivity.class);
        mLocationManager = new LocationManager(this);

        // If the incoming intent contains an update
        if (ActivityRecognitionResult.hasResult(intent)) {
            // Get the update
            ActivityRecognitionResult result =
                    ActivityRecognitionResult.extractResult(intent);
            // Get the most probable activity
            DetectedActivity mostProbableActivity =
                    result.getMostProbableActivity();

            // Write this to the db
            logActivity(mostProbableActivity);
        } else {
            /*
             * This implementation ignores intents that don't contain
             * an activity update. If you wish, you can report them as
             * errors.
             */
        }
    }

    private void logActivity(DetectedActivity activity){
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