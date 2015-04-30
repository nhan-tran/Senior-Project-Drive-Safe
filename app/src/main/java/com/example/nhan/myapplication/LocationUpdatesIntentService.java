package com.example.nhan.myapplication;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nhan.myapplication.AppPrefs.AppPrefs;
import com.example.nhan.myapplication.SQLite.DriveSafeProvider;
import com.example.nhan.myapplication.SQLite.DrivingDataContract;
import com.google.android.gms.location.LocationClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class handles location updates that were initially requested by the LocationRequestor class.
 *
 */
public class LocationUpdatesIntentService extends IntentService {

    public LocationUpdatesIntentService(){
        super("LocationUpdatesIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // http://www.intelligrape.com/blog/background-location-updates-on-android/
        // OMG thank god... finally
        Location location = (Location) intent.getExtras().get(LocationClient.KEY_LOCATION_CHANGED);

        // write the location to db
        if (location != null)
        {
            DriveSafeProvider db = new DriveSafeProvider(this);
            ContentValues values = new ContentValues();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_CREATED_DATE, dateFormat.format(date));
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_SPEED, location.getSpeed());
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_LONGITUDE, location.getLongitude());
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_LATITUDE, location.getLatitude());
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_LOCATION_TIME, location.getTime());
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_BEARING, location.getBearing());
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_ANDROID_ID, AppPrefs.GetAndroidId());
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_MEMBERSHIP_ID, AppPrefs.GetUserMemberId());
            values.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_ACCURACY, location.getAccuracy());

            db.InsertRecord(DrivingDataContract.LOCATION_LOG.TABLE_NAME, values);
        }
    }

    protected void SyncToWeb(ContentValues values)
    {
        boolean success = true;
        JSONObject jsonObj = new JSONObject();

        String createdDate = values.getAsString(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_CREATED_DATE);
        Double speed = values.getAsDouble(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_SPEED);
        Double latitude = values.getAsDouble(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_LATITUDE);
        Double longitude =values.getAsDouble(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_LONGITUDE);
        Long locationTime = values.getAsLong(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_LOCATION_TIME);
        String androidId = values.getAsString(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_ANDROID_ID);
        String membershipId = values.getAsString(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_MEMBERSHIP_ID);
        int sync = values.getAsInteger(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_SYNCED);
        Double bearing = values.getAsDouble(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_BEARING);
        Double accuracy = values.getAsDouble(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_ACCURACY);
        Long _ID = values.getAsLong(DrivingDataContract.LOCATION_LOG._ID);

        try {
            jsonObj.put("Created_Date", createdDate.toString());
            jsonObj.put("Speed", speed);
            jsonObj.put("Latitude", latitude);
            jsonObj.put("Longitude", longitude);
            jsonObj.put("Location_Time", locationTime);
            jsonObj.put("Android_Id", androidId);
            jsonObj.put("Membership_Id", membershipId);
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
    }
}
