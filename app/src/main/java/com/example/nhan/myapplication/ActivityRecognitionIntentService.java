package com.example.nhan.myapplication;

/**
 * Created by Nhan on 11/19/2014.
 */

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Service that receives ActivityRecognition updates. It receives
 * updates in the background, even if the main Activity is not visible.
 */
public class ActivityRecognitionIntentService extends IntentService {

    public ActivityRecognitionIntentService(String name)
    {
        super(name);
    }
    public ActivityRecognitionIntentService()
    {
        super("default");
    }
    /**
     * Called when a new activity detection update is available.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Intent sendIntent = new Intent(this, MainActivity.class);

        // If the incoming intent contains an update
        if (ActivityRecognitionResult.hasResult(intent)) {
            // Get the update
            ActivityRecognitionResult result =
                    ActivityRecognitionResult.extractResult(intent);
            // Get the most probable activity
            DetectedActivity mostProbableActivity =
                    result.getMostProbableActivity();
            /*
             * Get the probability that this activity is the
             * the user's actual activity
             */
            int confidence = mostProbableActivity.getConfidence();
            /*
             * Get an integer describing the type of activity
             */
            int activityType = mostProbableActivity.getType();
            String activityName = getNameFromType(activityType);
            /*
             * At this point, you have retrieved all the information
             * for the current update. You can display this
             * information to the user in a notification, or
             * send it to an Activity or Service in a broadcast
             * Intent.
             */

            // send the DetectedActivity back to Main
            //return mostProbableActivity;
            sendIntent.putExtra("probableActivity", activityName);
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //http://stackoverflow.com/questions/3606596/android-start-activity-from-service
            startActivity(sendIntent);
        } else {
            /*
             * This implementation ignores intents that don't contain
             * an activity update. If you wish, you can report them as
             * errors.
             */
        }



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
        }
        return "unknown";
    }

}