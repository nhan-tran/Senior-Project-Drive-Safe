package com.example.nhan.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nhan.myapplication.Enums.RequestType;
import com.example.nhan.myapplication.SQLite.DriveSafeProvider;
import com.example.nhan.myapplication.SQLite.DrivingDataContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.LocationClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {
    
    // Global constants
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // Constants that define the activity detection interval
    public static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int DETECTION_INTERVAL_SECONDS = 10;
    public static final int DETECTION_INTERVAL_MILLISECONDS =
            MILLISECONDS_PER_SECOND * DETECTION_INTERVAL_SECONDS;

    // define enum
    public enum REQUEST_TYPE {START, STOP}

    private REQUEST_TYPE mRequestType;

    private LocationClient mLocationClient;

    // Flag that indicates if a request is underway.
    private boolean mInProgress;
    // activity status
    private PendingIntent mActivityRecognitionPendingIntent;
    private ActivityRecognitionClient mActivityRecognitionClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInProgress = false;

        mActivityRecognitionClient = new ActivityRecognitionClient(this, this, this);
        Intent intent = new Intent(this, ActivityRecognitionIntentService.class);

        mActivityRecognitionPendingIntent =
                PendingIntent.getService(this, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
    }   // onCreate()

    /**
     * Request activity recognition updates based on the current
     * detection interval.
     *
     */
    public void startUpdates() {

        Context ctx = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        prefsEdit.putBoolean("isDrivingForSure", false);
        prefsEdit.commit(); // commit the edit!

        // Set the request type to START
        mRequestType = REQUEST_TYPE.START;

        // Check for Google Play services
        if (!servicesConnected()) {
            return;
        }
        // If a request is not already underway
        if (!mInProgress) {
            // Indicate that a request is in progress
            mInProgress = true;
            // Request a connection to Location Services
            mActivityRecognitionClient.connect();
            //
        } else {
            /*
             * A request is already underway. You can handle
             * this situation by disconnecting the client,
             * re-setting the flag, and then re-trying the
             * request.
             */
            Toast.makeText(this, "Cancelling the request in progress... Reconnecting... ", Toast.LENGTH_LONG).show();
            mActivityRecognitionClient.connect();
        }
    }

    /*

     */
    public void stopActivityUpdates(View view)
    {
        stopUpdates();
    }

    /**
     * Turn off activity recognition updates
     *
     */
    public void stopUpdates() {
        // Set the request type to STOP
        mRequestType = REQUEST_TYPE.STOP;
        /*
         * Test for Google Play services after setting the request type.
         * If Google Play services isn't present, the request can be
         * restarted.
         */
        if (!servicesConnected()) {
            return;
        }
        // If a request is not already underway
        if (!mInProgress) {
            // Indicate that a request is in progress
            mInProgress = true;
            // Request a connection to Location Services
            mActivityRecognitionClient.connect();
            //
        } else {
             /*
             * A request is already underway. You can handle
             * this situation by disconnecting the client,
             * re-setting the flag, and then re-trying the
             * request.
             */
            Toast.makeText(this, "Cancelling the request in progress... Reconnecting... ", Toast.LENGTH_LONG).show();
            mActivityRecognitionClient.disconnect();
            mActivityRecognitionClient.connect();
        }

        ToggleStartStopButton();
    }
    /*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();

        ToggleStartStopButton();
    }

    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        //mLocationClient.disconnect();
        super.onStop();
        SetIsMonitoring(false);
    }

    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        switch (mRequestType) {
            case START :
                SetIsMonitoring(true);

                Toast.makeText(this, "Connected to Start", Toast.LENGTH_SHORT).show();
                /*
                 * Request activity recognition updates using the
                 * preset detection interval and PendingIntent.
                 * This call is synchronous.
                 */
                mActivityRecognitionClient.requestActivityUpdates(
                        DETECTION_INTERVAL_MILLISECONDS,
                        mActivityRecognitionPendingIntent);
                break;
            case STOP :
                SetIsMonitoring(false);

                Toast.makeText(this, "Connected to Stop", Toast.LENGTH_SHORT).show();
                mActivityRecognitionClient.removeActivityUpdates(
                        mActivityRecognitionPendingIntent);
                break;
                /*
                 * An enum was added to the definition of REQUEST_TYPE,
                 * but it doesn't match a known case. Throw an exception.
                 */
            default :
                try {
                    throw new Exception("Unknown request type in onConnected().");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }      /*
        /*
         * Since the preceding call is synchronous, turn off the
         * in progress flag and disconnect the client
         */
        mInProgress = false;
        mActivityRecognitionClient.disconnect();
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
        // Turn off the request flag
        mInProgress = false;
        // Delete the client
        mActivityRecognitionClient = null;
        SetIsMonitoring(false);
    }

    /*
       * Called by Location Services if the attempt to
       * Location Services fails.
       */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            showErrorDialog(connectionResult.getErrorCode());
        }
        SetIsMonitoring(false);
    }

    void showErrorDialog(int code) {
        GooglePlayServicesUtil.getErrorDialog(code, this, 00000
               ).show();
    }

    public void getStatus(View view) {

        startUpdates();
       /* Boolean isConnected = servicesConnected();
*/
    }

    public void startLocationUpdates(View view){
        LocationRequestor requestor = new LocationRequestor(this, RequestType.START);
    }
    public void stopLocationUpdates(View view){
       LocationRequestor requestor = new LocationRequestor(this, RequestType.STOP);
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    /*
                     * Try the request again
                     */
                        
                        break;
                }
                
        }
    }
    
    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason.
            // resultCode holds the error code.
        }
        else
        {
            // Get the error dialog from Google Play services
            GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST).show();
                    // http://stackoverflow.com/a/16850856
            return false;
        }
    }

    public void toggleSilentRingerMode(View view)
    {
        Intent dbmanager = new Intent(this,AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }

    // for each record from location log that is not synced, POST it to the api
    public void SyncDataPerRecord(View view)
    {
        boolean success = true;
        DriveSafeProvider db = new DriveSafeProvider(this);
        int maxSyncCount = 10;
        int syncCount = 0;

        // Testing if querying my Provider works?!?!
        // Does a query against the table and returns a Cursor object
        Uri contentUri = Uri.parse("content://com.example.nhan.myapplication.DriveSafeProvider");
        final Cursor mCursor = getContentResolver().query(
                contentUri,  // The content URI of the words table
                null,                       // The columns to return for each row
                null,                   // Either null, or the word the user entered
                null,                    // Either empty, or the string the user entered
                "");                       // The sort order for the returned rows

        JSONObject jsonObj = new JSONObject();

        mCursor.moveToFirst();

        while (!mCursor.isAfterLast() && syncCount < maxSyncCount) {
            String createdDate = mCursor.getString(mCursor.getColumnIndex(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_CREATED_DATE));
            Double speed = mCursor.getDouble(mCursor.getColumnIndex(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_SPEED));
            Double latitude = mCursor.getDouble(mCursor.getColumnIndex(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_LATITUDE));
            Double longitude = mCursor.getDouble(mCursor.getColumnIndex(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_LONGITUDE));
            Long locationTime = mCursor.getLong(mCursor.getColumnIndex(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_LOCATION_TIME));
            String userId = mCursor.getString(mCursor.getColumnIndex(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_USER_ID));
            int sync = mCursor.getInt(mCursor.getColumnIndex(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_SYNCED));
            Double bearing = mCursor.getDouble(mCursor.getColumnIndex(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_BEARING));
            Double accuracy = mCursor.getDouble(mCursor.getColumnIndex(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_ACCURACY));
            Long _ID = mCursor.getLong(mCursor.getColumnIndex(DrivingDataContract.LOCATION_LOG._ID));

            try {
                jsonObj.put("Created_Date", createdDate.toString());
                jsonObj.put("Speed", speed);
                jsonObj.put("Latitude", latitude);
                jsonObj.put("Longitude", longitude);
                jsonObj.put("Location_Time", locationTime);
                jsonObj.put("User_Id", userId);
                jsonObj.put("Synced", sync);
                jsonObj.put("Bearing", bearing);
                jsonObj.put("Accuracy", accuracy);
                jsonObj.put("_ID_Android", _ID);

                RequestQueue queue = Volley.newRequestQueue(this);  // this = context
                String url = "http://drivesafe-dev.azurewebsites.net/api/Location_Log_Sync_";

                JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObj, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        DriveSafeProvider db = new DriveSafeProvider(getApplicationContext());
                        Log.i("volley", "response: " + response);

                        String _id = "0";
                        try {
                            _id = response.getString("_ID_Android");
                        }
                        catch (Exception e){
                            Log.d("Reponse JSON error", "JSON error i onRepsonse");
                        }
                        ContentValues cv = new ContentValues();
                        cv.put("SYNCED", 1);
                        db.UpdateLogRecord(cv, _id);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("volley", "error: " + error);
                    }
                });
                queue.add(postRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            syncCount++;
            mCursor.moveToNext();
        }   // while

        mCursor.close();

        Toast.makeText(this,"Syncing...", Toast.LENGTH_LONG).show();
        //return true;
    }

    private void ToggleStartStopButton()
    {
        Button startButton = (Button) findViewById(R.id.btn_activity_status);
        Button stopButton = (Button) findViewById(R.id.btn_stop_updates);
        TextView status = (TextView) findViewById(R.id.textView_status);

        if (IsMonitoring())
        {
            // show stop button
            startButton.setVisibility(View.GONE);
            stopButton.setVisibility(View.VISIBLE);
            status.setText("Monitoring is ON");
        }
        else
        {
            // show start button
            startButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.GONE);
            status.setText("Monitoring is OFF");
        }
    }

    protected void SetIsMonitoring(boolean status){
        Context ctx = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        prefsEdit.putBoolean("isMonitoring", status);
        prefsEdit.commit(); // commit the edit!
    }

    protected boolean IsMonitoring(){
        Context ctx = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        boolean status = prefs.getBoolean("isMonitoring", false);

        return status;
    }
}